package org.booking.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.muserver.rest.ApiResponse;
import io.muserver.rest.Description;
import lombok.extern.slf4j.Slf4j;
import org.booking.domain.Booking;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

//TODO centralized exceptions and validations
//TODO environment specific configurations.
@Slf4j
@Path("/booking")
@Description(value = "A Booking System", details = "Use this API to get/schedule the bookings")
public class BookingController {

    private final List<Booking> bookings;

    public BookingController(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Gets a given booking slot")
    @ApiResponse(code = "200", message = "Success")
    @ApiResponse(code = "404", message = "No slots found")
    public String getBooking(@Description("The ID of the booking")
                             @PathParam("id") int id, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("User")) {
            throw new ClientErrorException("This requires a User role", 403);
        }

        List<Booking> booking = bookings.stream().filter(entry -> entry.getId() == id).collect(Collectors.toList());
        if (booking == null || booking.size() == 0) {
            throw new NotFoundException("No booking with id " + id);
        }
        return new JSONObject()
                .put("id", id)
                .put("details", booking.get(0))
                .toString(4);
    }

    @POST
    @Path("/book")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Creates a new booking")
    @ApiResponse(code = "200", message = "The booking is created")
    @ApiResponse(code = "400", message = "The booking details are not specified")
    public Response book(@Description("The Booking information") Booking booking, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("User")) {
            throw new ClientErrorException("This requires a User role", 403);
        }

        bookings.add(booking);
        return Response.status(200).build();
    }


    @GET
    @Path("/retrieve/{dateStr}")
    @Produces(MediaType.APPLICATION_JSON)
    @Description("Gets all bookings")
    @ApiResponse(code = "200", message = "Success")
    @ApiResponse(code = "404", message = "No booking found")
    public String getBookingData(@Description("The booking date")
                                 @PathParam("dateStr") String dateStr, @Context SecurityContext securityContext) {
        if (!securityContext.isUserInRole("Admin")) {
            throw new ClientErrorException("This requires an Admin role", 403);
        }

        if (bookings == null) {
            throw new NotFoundException("No bookings are found ");
        }

        List<Booking> collectList = bookings.stream().filter(enty ->
                enty.getDateStr().equals(dateStr)).collect(Collectors.toList());

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(collectList);
    }

}
