package com.lulu.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lulu.dao.UserDao;
import com.lulu.model.User;

@MultipartConfig
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static String INSERT_OR_EDIT = "/user.jsp";
    private static String LIST_USER = "/listUser.jsp";
    private UserDao dao;

    public UserController() {
        super();
        dao = new UserDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward = "";
        String action = request.getParameter("action");

        if (action.equalsIgnoreCase("delete")) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            dao.deleteUser(userId);
            forward = LIST_USER;
            request.setAttribute("users", dao.getAllUsers());
        } else if (action.equalsIgnoreCase("edit")) {
            forward = INSERT_OR_EDIT;
            int userId = Integer.parseInt(request.getParameter("userId"));
            User user = dao.getUserById(userId);
            request.setAttribute("user", user);
        } else if (action.equalsIgnoreCase("listUser")) {
            forward = LIST_USER;
            request.setAttribute("users", dao.getAllUsers());
        }else if (action.equalsIgnoreCase("download")){
            forward = LIST_USER;
            downloadUserFile(request, response);
        }
        else {
            forward = INSERT_OR_EDIT;
        }

        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    private void downloadUserFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String appPath = request.getServletContext().getRealPath("web/resources/users/") + request.getParameter("userId") + "/file/";
        User user = dao.getUserById(Integer.parseInt(request.getParameter("userId")));
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + user.getFileName() + "\"");

        FileInputStream fileInputStream = new FileInputStream(appPath
                + user.getFileName());

        int i;
        while ((i = fileInputStream.read()) != -1) {
            out.write(i);
        }
        fileInputStream.close();
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        InputStream inputStream = null;
        boolean isUpdate = false;
        try {
            Date dob = new SimpleDateFormat("MM/dd/yyyy").parse(request.getParameter("dob"));
            user.setDob(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Part filePart = request.getPart("file");
        user.setEmail(request.getParameter("email"));
        String userid = request.getParameter("userid");
        if (userid == null || userid.isEmpty()) {
            user.setFileName(filePart.getSubmittedFileName());
            user = dao.addUser(user);
            isUpdate = true;
        } else {
            User userFromDb = dao.getUserByEmail(user.getEmail());
            if(!filePart.getSubmittedFileName().isEmpty()){
                if (userFromDb.getFileName() != filePart.getSubmittedFileName()){
                    user.setFileName(filePart.getSubmittedFileName());
                    isUpdate = true;
                }
            }
            user.setUserid(Integer.parseInt(userid));
            if(user.getFileName() == null)
                user.setFileName(userFromDb.getFileName());
            dao.updateUser(user);
        }
        if(isUpdate)
            saveUserResource(request, user, filePart, "file");
        RequestDispatcher view = request.getRequestDispatcher(LIST_USER);
        request.setAttribute("users", dao.getAllUsers());
        view.forward(request, response);
    }

    private void saveUserResource(HttpServletRequest request, User user, Part filePart, String resourceType) throws IOException {
        InputStream inputStream;
        if (filePart != null) {
            inputStream = filePart.getInputStream();
            String appPath = request.getServletContext().getRealPath("web/resources/users/") + user.getUserid() + "/" + resourceType;
            String fullSavePath = appPath.replace('\\', '/');
            File fileSaveDir = new File(fullSavePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            try {
                for (File file : fileSaveDir.listFiles())
                    if (!file.isDirectory())
                        file.delete();
                filePart.write(fileSaveDir + "/" + user.getFileName());
            } catch (Exception e) {
                System.out.println("User file not found");
                e.printStackTrace();
            }

        }
    }

}