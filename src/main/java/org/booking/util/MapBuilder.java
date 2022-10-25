package org.booking.util;

import java.util.HashMap;

public class MapBuilder extends HashMap<String, Object> {
    public MapBuilder with(String key, Object value) {
        put(key, value);
        return this;
    }
}


//
//    HttpsConfigBuilder httpsConfig = HttpsConfigBuilder.httpsConfig()
//            .withKeystoreType("JKS")
//            .withKeystorePassword("Very5ecure")
//            .withKeyPassword("ActuallyNotSecure")
//            .withKeystore(new File("src/main/java/io/muserver/docs/samples/HttpsCert.jks"))
//            .withProtocols("TLSv1.2", "TLSv1.3")
//            .withCipherFilter(new SSLCipherFilter() {
//                public List<String> selectCiphers(Set<String> supportedCiphers, List<String> defaultCiphers) {
//                    return defaultCiphers;
//                }
//            });
//
//    MuServer server = muServer()
//            .withHttpsPort(10443)
//            .withHttpsConfig(httpsConfig)
//            .addHandler(Method.GET, "/", (req, resp, pp) -> resp.write("This is HTTPS"))
//            .start();


//        MuServer server = httpsServer()
//                .addHandler(
//                        RestHandlerBuilder.restHandler(new Thing())
//                                .addRequestFilter(new BasicAuthSecurityFilter("My-App", authenticator, authorizer))
//                )
//                .addHandler(Method.GET, "/", (request, response, pathParams) -> {
//                    response.contentType("text/html");
//                    response.write(getDemoPageHtml());
//                })
//                .start();




//    @GET
//    @Path("/read")
//    public String readStuff(@Context SecurityContext securityContext) {
//        if (!securityContext.isUserInRole("User")) {
//            throw new ClientErrorException("This requires a User role", 403);
//        }
//        return "Reading stuff securely? " + securityContext.isSecure();
//    }
//
//    @GET
//    @Path("/admin")
//    public String doAdmin(@Context SecurityContext securityContext) {
//        if (!securityContext.isUserInRole("Admin")) {
//            throw new ClientErrorException("This requires an Admin role", 403);
//        }
//        return "Admin Only Access";
//    }

//}


//@Path("/things")
//@Produces("text/plain")
//public class Thing {
//
//    @GET
//    @Path("/read")
//    public String readStuff(@Context SecurityContext securityContext) {
//        if (!securityContext.isUserInRole("User")) {
//            throw new ClientErrorException("This requires a User role", 403);
//        }
//        return "Reading stuff securely? " + securityContext.isSecure();
//    }
//
//    @GET
//    @Path("/admin")
//    public String doAdmin(@Context SecurityContext securityContext) {
//        if (!securityContext.isUserInRole("Admin")) {
//            throw new ClientErrorException("This requires an Admin role", 403);
//        }
//        return "Doing admin";
//    }
//
//}


//    @Path("/users")
//    @Description(value = "A human user", details = "Use this API to get and create users")
//    public static class UserResource {
//
//        @GET
//        @Path("/{id}")
//        @Produces("application/json")
//        @Description("Gets a single user")
//        @ApiResponse(code = "200", message = "Success")
//        @ApiResponse(code = "404", message = "No user with that ID found")
//        public String get(
//                @Description("The ID of the user")
//                @PathParam("id") int id) {
//            return new JSONObject()
//                    .put("id", id)
//                    .toString(4);
//        }
//
//        @POST
//        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//        @Description("Creates a new user")
//        @ApiResponse(code = "201", message = "The user was created")
//        @ApiResponse(code = "400", message = "The ID or name was not specified")
//        public Response create(
//                @Description("A unique ID for the new user")
//                @Required @FormParam("id") int id,
//                @Description("The name of the user")
//                @FormParam("name") String name) {
//            return Response.status(201).build();
//        }
//
//    }