package com.rooxteam.uidm.sdk.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

public class ServletForTesting extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        try {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            out.println("<title>Servlet for auth filter testing</title></head>");
            out.println("<body>");
            out.println("<h1>This is a response from testing servlet</h1>");  // says Hello
            // Echo client's request information
            out.println("<p>Request URI:   " + request.getRequestURI() + "</p>");
            out.println("<p>Remote user:   " + request.getRemoteUser() + "</p>");
            for (Object header : Collections.list(request.getHeaderNames())) {
                out.println("<p>Request header:   " + header.toString() + " : " + request.getHeader(header.toString()) + "</p>");
            }
            for (Object attr : Collections.list(request.getAttributeNames())) {
                out.println("<p>Request attribute:   " + attr.toString() + " = " + request.getHeader(attr.toString()) + "</p>");
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();  // Always close the output writer
        }
    }
}
