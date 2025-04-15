package in.basha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.basha.dto.AuthRequest;
import in.basha.dto.AuthResponse;
import in.basha.entity.Teacher;
import in.basha.service.JwtService;
import in.basha.service.TeacherService;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
	private AuthenticationManager authManager;

    // 🔹 Register Teacher
    @PostMapping("/register")
    public ResponseEntity<Teacher> register(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.registerTeacher(teacher));
    }

    // 🔹 Login Teacher
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    	authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Teacher teacher = teacherService.getTeacherByEmail(request.getEmail());
        String token = jwtService.generateToken(
            new org.springframework.security.core.userdetails.User(
                teacher.getEmail(),
                teacher.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_TEACHER"))
            )
        );

        AuthResponse response = new AuthResponse(token, teacher.getEmail(), teacher.getName());
        return ResponseEntity.ok(response);
    }


    // 🔹 Admit student using Feign
    @PostMapping("/{teacherId}/admit/{studentId}")
    public ResponseEntity<?> admitStudent(@PathVariable Long teacherId, @PathVariable Long studentId) {
        return ResponseEntity.ok(teacherService.admitStudent(studentId, teacherId));
    }
}
