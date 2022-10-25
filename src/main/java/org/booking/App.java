package org.booking;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.muserver.*;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.Authorizer;
import io.muserver.rest.BasicAuthSecurityFilter;
import io.muserver.rest.RestHandlerBuilder;
import io.muserver.rest.UserPassAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.booking.controller.BookingController;
import org.booking.domain.Booking;

import javax.ws.rs.ClientErrorException;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;
import static io.muserver.rest.CORSConfigBuilder.corsConfig;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

@Slf4j
public class App {
    /*
    Users and Roles are hardcoded to verify the appropriate access for API endpoints.
     */
    private static final Map<String, Map<String, List<String>>> usersToPasswordToRoles = new HashMap<>();

    public static void main(String[] args) {
/*
TODO enable https to secury the communications.
 */
        MuServer server = MuServerBuilder.httpServer().withHttpPort(8080) //httpsserver()
                .addHandler(createRestHandler())
                .addHandler(new LoggingHandler())
                .addHandler(new HostHeaderChecker())
                .start();

        log.debug("API HTML: " + server.uri().resolve("/api.html"));
        log.debug("API JSON: " + server.uri().resolve("/openapi.json"));

    }

    public static RestHandlerBuilder createRestHandler() {
        /*
        TODO can be integrated to the real database for user access verifications.
         */
        usersToPasswordToRoles.put("Ravi", singletonMap("s@curePa55word!", asList("User", "Admin")));
        usersToPasswordToRoles.put("John", singletonMap("password123", asList("User")));
        MyUserPassAuthenticator authenticator = new MyUserPassAuthenticator(usersToPasswordToRoles);
        MyAuthorizer authorizer = new MyAuthorizer();

        return RestHandlerBuilder.restHandler(new BookingController(initialize()))
                .addCustomWriter(new JacksonJaxbJsonProvider())//Added custom json handlers
                .addCustomReader(new JacksonJaxbJsonProvider())
                .addRequestFilter(new BasicAuthSecurityFilter("Booking-App", authenticator, authorizer))
                .withCORS(//corsConfig().withAllowedOriginRegex(".*")
                        corsConfig()
                                .withAllowedOriginRegex("http(s)?://localhost:[0-9]+"))
                .withOpenApiHtmlUrl("/api.html") // OpenAPI for easy documentation
                .withOpenApiJsonUrl("/openapi.json")
                .withOpenApiDocument(
                        OpenAPIObjectBuilder.openAPIObject()
                                .withInfo(
                                        infoObject()
                                                .withTitle("Booking API Documentation")
                                                .withDescription("This is just a booking demo API!")
                                                .withVersion("1.0")
                                                .build())
                                .withExternalDocs(
                                        externalDocumentationObject()
                                                .withDescription("Documentation docs")
                                                .withUrl(URI.create("https://muserver.io/jaxrs"))
                                                .build()
                                )
                );
    }

    private static List<Booking> initialize() {
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking(1, "John", LocalDate.now().toString(), LocalDateTime.now().toString(), 4);
        Booking booking2 = new Booking(2, "Ravi", LocalDate.now().toString(), LocalDateTime.now().toString(), 5);
        Booking booking3 = new Booking(3, "Mario", LocalDate.now().toString(), LocalDateTime.now().toString(), 1);
        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);

        return bookings;
    }
}

/// Log the request method, path, and IP address
@Slf4j
class LoggingHandler implements MuHandler {
    public boolean handle(MuRequest request, MuResponse response) {
        log.debug("Recieved " + request + " from " + request.remoteAddress());
        return false; // so that the next handler is invoked
    }
}

// Block any requests where the Host header is not localhost
@Slf4j
class HostHeaderChecker implements MuHandler {
    public boolean handle(MuRequest request, MuResponse response) {
        if (!request.uri().getHost().equals("localhost")) {
            throw new ClientErrorException("The host header must be 'localhost'", 400);
        }
        return false;
    }
}
@Slf4j
class MyUser implements Principal {
    private final String name;
    private final List<String> roles;

    MyUser(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isInRole(String role) {
        return roles.contains(role);
    }
}
@Slf4j
class MyUserPassAuthenticator implements UserPassAuthenticator {
    private final Map<String, Map<String, List<String>>> usersToPasswordToRoles;

    public MyUserPassAuthenticator(Map<String, Map<String, List<String>>> usersToPasswordToRoles) {
        this.usersToPasswordToRoles = usersToPasswordToRoles;
    }

    @Override
    public Principal authenticate(String username, String password) {
        Principal principal = null;
        Map<String, List<String>> user = usersToPasswordToRoles.get(username);
        if (user != null) {
            List<String> roles = user.get(password);
            if (roles != null) {
                principal = new MyUser(username, roles);
            }
        }
        return principal;
    }
}
@Slf4j
class MyAuthorizer implements Authorizer {
    @Override
    public boolean isInRole(Principal principal, String role) {
        if (principal == null) {
            return false;
        }
        MyUser user = (MyUser) principal;
        return user.isInRole(role);
    }
}

