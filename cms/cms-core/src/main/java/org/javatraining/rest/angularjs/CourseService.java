package org.javatraining.rest.angularjs;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

//@Stateless
@ApplicationPath("rest")
@Path("/courses")
public class CourseService extends Application {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> listPersons() {
        List<Course> list = new ArrayList<>();
        list.add(new Course("Java EE", "Description for Java EE"));
        list.add(new Course("Java SE", "Description for Java SE"));
        list.add(new Course("Android", "Description for Android"));
        return list;
    }
}
