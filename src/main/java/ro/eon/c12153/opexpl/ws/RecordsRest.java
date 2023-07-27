package ro.any.c12153.opexpl.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import ro.any.c12153.opexpl.services.RecordsActualServ;
import ro.any.c12153.opexpl.services.RecordsPlanServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
@Path("recs")
public class RecordsRest {
    private static final Logger LOG = Logger.getLogger(RecordsRest.class.getName());
    
    @Context
    private UriInfo context;
    
    public RecordsRest(){
    }
    
    @POST
    @Path(value = "/actual/rest/xlsx")
    public Response getActualRecords(@FormParam("coarea") String coarea,
            @FormParam("dataset") String set,
            @FormParam("filter") String filter,
            @Context SecurityContext scontext){
        
        String userId = scontext.getUserPrincipal().getName();
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    RecordsActualServ.recordsToXlsx(coarea, Integer.valueOf(set),
                            (Utils.stringNotEmpty(filter) ? Optional.ofNullable(Utils.jsonStringToMap(filter, true)) : Optional.empty()),
                            userId, output);
                    
                } catch (Exception ex) {
                    App.log(LOG, Level.SEVERE, userId, ex);
                }
            }
        };
        return Response.ok(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=raport.xlsx")
                .build();
    }
    
    @POST
    @Path(value = "/plan/rest/xlsx")
    public Response getPlanRecords(@FormParam("coarea") String coarea,
            @FormParam("dataset") String set,
            @FormParam("filter") String filter,
            @Context SecurityContext scontext){
        
        String userId = scontext.getUserPrincipal().getName();
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    RecordsPlanServ.recordsToXlsx(coarea, Integer.valueOf(set),
                            (Utils.stringNotEmpty(filter) ? Optional.ofNullable(Utils.jsonStringToMap(filter, true)) : Optional.empty()),
                            userId, output);
                    
                } catch (Exception ex) {
                    App.log(LOG, Level.SEVERE, userId, ex);
                }
            }
        };
        return Response.ok(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=raport.xlsx")
                .build();
    }
}
