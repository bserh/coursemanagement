package org.javatraining.integration.google.drive.mocks;

import org.javatraining.integration.google.drive.DriveService;
import org.javatraining.model.PersonVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("drive")
public class MockDriveController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MockDriveController.class);
    private static final String PARAM_METHOD = "_method";
    private static final String PARAM_EMAIL = "email";
    @Inject
    DriveService driveService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String param = req.getParameter(PARAM_METHOD);
        if (param.equals("get")) {
            driveService.getFilesList();
            resp.getWriter().write("list was successfully returned; see log");
            return;
        } else if (param.equals("delete_files")) {
            driveService.removeFiles();
            resp.getWriter().write("all files were deleted");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String param = req.getParameter(PARAM_EMAIL);
        if (param != null) {
            PersonVO personVO = new PersonVO();
            personVO.setEmail(param);
            driveService.changeRootFolderOwner(personVO);
            resp.getWriter().write("owner is added successfully");
            return;
        }
        resp.getWriter().write("error with passing parameter");
    }
}
