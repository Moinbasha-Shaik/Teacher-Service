package in.basha.client;

import in.basha.dto.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name = "student-service", url = "http://localhost:8081/api/student") // update port if different
public interface StudentService {

    @GetMapping("/{id}")
    Optional<Student> getStudentById(@PathVariable("id") Long id);

    @PutMapping("/update")
    void addStudent(@RequestBody Student student); 
}
