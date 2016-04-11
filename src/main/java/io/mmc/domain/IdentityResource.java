package io.mmc.domain;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

/**
 * Created by charlesmarvin on 4/8/16.
 */
@Path("/identities")
@Produces(MediaType.APPLICATION_JSON)
@Api("Identity Service")
public class IdentityResource {
    private final IdentityService identityService;

    public IdentityResource(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get all Identities", response = Identity.class, responseContainer = "Collection")
    public void getByPhoneOrEmail(@Suspended final AsyncResponse asyncResponse) {
        identityService.findAll()
                .thenAccept(asyncResponse::resume)
                .exceptionally(getExceptionHandler(asyncResponse));
    }

    @GET
    @Path("/{principal}")
    @Timed
    @ApiOperation("Search Identity by principal")
    public void getByPhoneOrEmail(@Suspended final AsyncResponse asyncResponse,
                                  @ApiParam(value = "Identity Principal", required = true) @PathParam("principal") String principal) {
        if ((StringUtils.isBlank(principal))) {
            throw new WebApplicationException("Principal is required", Response.Status.BAD_REQUEST);
        }
        identityService.getIdentityByPrincipal(principal)
                .thenAccept(identityOpt -> identityOpt.map(asyncResponse::resume)
                        .orElseThrow(() -> new WebApplicationException("No such identity exists.", Response.Status.NOT_FOUND)))
                .exceptionally(getExceptionHandler(asyncResponse));
    }

    @PUT
    @Path("/{principal}")
    @Timed
    @ApiOperation(value = "Update Identity validity by email or phone", response = Identity.class)
    public void updateValidity(@Suspended final AsyncResponse asyncResponse,
                               @ApiParam(value = "Identity Principal", required = true) @PathParam("principal") String principal,
                               @ApiParam(value = "Updated Identity", required = true) Identity identity) {
        if (StringUtils.isBlank(identity.getPrincipal())
                || !identity.getPrincipal().equals(principal)
                || identity.getState() == null) {
            throw new WebApplicationException("Bad input", Response.Status.BAD_REQUEST);
        }
        identityService.getIdentityByPrincipal(principal)
                .thenAccept(identityOpt -> identityOpt.map(storedIdentity -> {
                    storedIdentity.setState(identity.getState());
                    identityService.save(storedIdentity)
                            .thenRun(() -> asyncResponse.resume(storedIdentity))
                            .exceptionally(getExceptionHandler(asyncResponse));
                    return storedIdentity;
                })
                        .orElseThrow(() -> new WebApplicationException("No such identity exists.", Response.Status.NOT_FOUND)))
                .exceptionally(getExceptionHandler(asyncResponse));
    }

    @POST
    @Timed
    @ApiOperation(value = "Create a new Identity", response = Identity.class)
    public void createValidity(@Suspended final AsyncResponse asyncResponse,
                               @ApiParam(value = "New Identity", required = true) Identity identity) {
        if (StringUtils.isBlank(identity.getPrincipal()) && identity.getState() == null) {
            throw new WebApplicationException("Bad input", Response.Status.BAD_REQUEST);
        }
        identityService.save(identity)
                .thenRun(() -> asyncResponse.resume(identity))
                .exceptionally(getExceptionHandler(asyncResponse));
    }

    private Function<Throwable, Void> getExceptionHandler(AsyncResponse asyncResponse) {
        return e -> {
            //For cases where CompletionException is thrown get the actual exception
            if (e instanceof CompletionException) {
                e = e.getCause();
            }
            asyncResponse.resume(e);
            return null;
        };
    }
}
