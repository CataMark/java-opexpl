package ro.any.c12153.opexpl.ws;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import ro.any.c12153.opexpl.services.ModelServ;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
@Path("/model")
public class ModelRest {
    private static final Logger LOG = Logger.getLogger(ModelRest.class.getName());
    
    public ModelRest(){
    }
    
    @GET
    @Produces("text/csv")
    @Path(value = "/alocare/csv/{set}/{coarea}")
    public void getAlocareCsv(@Suspended final AsyncResponse asyncResponse,
                                @Context HttpServletRequest servletRequest,
                                @PathParam("set") Integer dataset, @PathParam("coarea") String coarea){
        
        if (servletRequest.isAsyncStarted()){
            final AsyncContext asyncContext = servletRequest.getAsyncContext();            
            try(BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(asyncContext.getResponse().getOutputStream(), StandardCharsets.UTF_8),
                    2 * 1024 * 1024
            );){
                ModelServ.toCsvAlocare(dataset, coarea, null, writer);
            } catch (Exception ex){
                if (!(ex instanceof java.io.IOException))
                    App.log(LOG, Level.SEVERE, null, ex);
            } finally {
                asyncContext.complete();
            }
        }
    }
    
    @GET
    @Produces("text/csv")
    @Path(value = "/alocare/csv/{set}/{coarea}/{tip}")
    public void getAlocareCsvByTip(@Suspended final AsyncResponse asyncResponse,
                                @Context HttpServletRequest servletRequest,
                                @PathParam("set") Integer dataset, @PathParam("coarea") String coarea, @PathParam("tip") String tip){
        
        if (servletRequest.isAsyncStarted()){
            final AsyncContext asyncContext = servletRequest.getAsyncContext();            
            try(BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(asyncContext.getResponse().getOutputStream(), StandardCharsets.UTF_8),
                    2 * 1024 * 1024
            );){
                ModelServ.toCsvAlocareByTip(dataset, coarea, tip, null, writer);
            } catch (Exception ex){
                if (!(ex instanceof java.io.IOException))
                    App.log(LOG, Level.SEVERE, null, ex);
            } finally {
                asyncContext.complete();
            }
        }
    }
    
    @GET
    @Produces("text/csv")
    @Path(value = "/alocare/csv/{set}/{coarea}/{tip}/{ccenter}")
    public void getAlocareCsvByCCenter(@Suspended final AsyncResponse asyncResponse,
                                @Context HttpServletRequest servletRequest,
                                @PathParam("set") Integer dataset, @PathParam("coarea") String coarea, @PathParam("tip") String tip, @PathParam("ccenter") String ccenter){
        
        if (servletRequest.isAsyncStarted()){
            final AsyncContext asyncContext = servletRequest.getAsyncContext();            
            try(BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(asyncContext.getResponse().getOutputStream(), StandardCharsets.UTF_8),
                    2 * 1024 * 1024
            );){
                ModelServ.toCsvAlocareByCCenter(dataset, coarea, tip, ccenter, null, writer);
            } catch (Exception ex){
                if (!(ex instanceof java.io.IOException))
                    App.log(LOG, Level.SEVERE, null, ex);
            } finally {
                asyncContext.complete();
            }
        }
    }
}
