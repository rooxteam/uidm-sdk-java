package com.rooxteam.uidm.sdk.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TestRedirectServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set the response message's MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        out.println("<title>Servlet for redirection</title></head>");
        out.println("<body>");
        out.println("<h1>This is a response from testing servlet</h1>");
        out.println("<h1>You have been redirected to this servlet because authentication has failed</h1>");
        out.println("</body>");
        out.println("</html>");
    }
}
