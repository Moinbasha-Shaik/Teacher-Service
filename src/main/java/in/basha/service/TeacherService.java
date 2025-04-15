package in.basha.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.basha.client.StudentService;
import in.basha.dto.Student;
import in.basha.entity.Teacher;
import in.basha.repo.TeacherRepository;

@Service
public class TeacherService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StudentService studentService; 
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    public Teacher registerTeacher(Teacher teacher) {
    	teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        return teacherRepository.save(teacher);
    }


    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Teacher not found"));
    }

   
    public Optional<Student> admitStudent(Long studentId, Long teacherId) {
        Optional<Student> student = studentService.getStudentById(studentId);

        if (student.isPresent()) {
            Student existingStudent = student.get();
            existingStudent.setTeacherId(teacherId);  
            studentService.addStudent(existingStudent); 
        }
        return student;
    }

    
    public String loginTeacher(String email, String password) {
        Teacher teacher = getTeacherByEmail(email);

        if (BCrypt.checkpw(password, teacher.getPassword())) {
            return jwtService.generateToken(loadUserByUsername(teacher.getEmail()));
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

  
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Teacher teacher = getTeacherByEmail(email);

        return new User(
            teacher.getEmail(),
            teacher.getPassword(),
            List.of(() -> "ROLE_TEACHER")  
        );
    }
}
