package ro.any.c12153.shared.aad;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ro.any.c12153.opexpl.AppSingleton;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Crypto;
import ro.any.c12153.shared.services.UserService;

@WebServlet(name = "AutorizareServlet", urlPatterns = {"/auth"})
public class AutorizareServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(AutorizareServlet.class.getName());
    

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            ServletContext context = request.getServletContext();
            String id_token_string = Optional.ofNullable(request.getParameter("id_token"))
                    .orElseThrow(() -> new Exception("NO_TOKEN"));            
            String state = request.getParameter("state");
            
            //set up token validator
            Issuer iss = new Issuer(context.getInitParameter("ro.any.c12153.AAD_ISSUER"));
            ClientID clientID = new ClientID(context.getInitParameter(AppSingleton.CHILD_APP ? "ro.any.c12153.CHILD_AAD_CLIENT_ID" : "ro.any.c12153.PARENT_AAD_CLIENT_ID"));
            JWSAlgorithm algorithm = JWSAlgorithm.RS256;
            URL jwKeySetURL = new URL(context.getInitParameter("ro.any.c12153.AAD_PUBLIC_KEYS"));
            IDTokenValidator validator = new IDTokenValidator(iss, clientID, algorithm, jwKeySetURL);
            validator.setMaxClockSkew(30);
            
            //performing validation
            JWT id_token = JWTParser.parse(id_token_string);
            Nonce expectedNonce = new Nonce((String) request.getSession(false).getAttribute("aad_nonce"));
            IDTokenClaimsSet claims = validator.validate(id_token, expectedNonce);
            request.getSession(false).removeAttribute("aad_nonce");
            
            String userMail = claims.getStringClaim("preferred_username");
            String userId = userMail.substring(0, userMail.indexOf('@'));
            
            String phrase = Crypto.decrypt(context.getInitParameter("ro.any.c12153.DEFAULT_PHRASE"));
            request.login(userId, UserService.pseudoPassGenerator(userId, phrase));
            response.sendRedirect(request.getContextPath().concat(state));
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
