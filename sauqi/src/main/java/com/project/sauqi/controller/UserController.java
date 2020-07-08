package com.project.sauqi.controller;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.UserRepo;
import com.project.sauqi.entity.User;
import com.project.sauqi.util.EmailUtil;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	private PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private EmailUtil emailUtil;
	
	
	 @GetMapping ("/members")
	 public Iterable<User> getAllUser(){ 
		 return userRepo.findAll(); 
	 }
	 
	
	@PostMapping
	public User registerUser(@RequestBody User user) {
		Optional<User> findUser = userRepo.findByUsername(user.getUsername());
		if (findUser.toString() != "Optional.empty") {
			throw new RuntimeException("Username exists!");
		}else {
			String encodedPassword = pwEncoder.encode(user.getPassword());
			String verifyToken = pwEncoder.encode(user.getUsername() + user.getEmail());
			
			user.setPassword(encodedPassword);
			user.setVerified(false);
			user.setVerifyToken(verifyToken);
			User savedUser = userRepo.save(user);
			savedUser.setPassword(null);
			
			String linkToVerify = "http://localhost:8080/users/verify/" + user.getUsername() + "?token=" + verifyToken;
			String message = "<h1>Selamat! Registrasi Berhasil</h1>\n";
			message += "Akun dengan username " + user.getUsername() + " telah terdaftar!\n";
			message += "Klik <a href=\"" + linkToVerify + "\">link ini</a> untuk verifikasi email anda.";
			emailUtil.sendEmail(user.getEmail(), "Registrasi Akun", message);
			return savedUser;
		}
	}
	
	@GetMapping("/verify/{username}")
	public String verifyUserEmail (@PathVariable String username, @RequestParam String token) {
		User findUser = userRepo.findByUsername(username).get();
		
		if (findUser.getVerifyToken().equals(token)) {
			findUser.setVerified(true);
		} else {
			throw new RuntimeException("Token is invalid");
		}
		
		userRepo.save(findUser);
		
		return "Sukses!";
	}
	
	@GetMapping
	public User getKeepLogin(@RequestParam int id) {
		User findUser = userRepo.findById(id).get();
		return findUser;
	}
	
	@GetMapping("/profile/{id}")
	public User getUserProfile(@PathVariable int id) {
		User findUser = userRepo.findById(id).get();
		return findUser;
	}
	
	@GetMapping("/login")
	public User getLoginUser(@RequestParam String username,@RequestParam String password) {
		User findUser = userRepo.findByUsername(username).get();
		Optional<User> findUserName = userRepo.findByUsername(username);
		
		
		if(findUserName.toString() != "Optional.empty") {
			if(pwEncoder.matches(password, findUser.getPassword())) {
				findUser.setPassword(null);
				return findUser;
			}else{
				throw new RuntimeException("Password salah!");
			}
			
		}else{
			throw new RuntimeException("Username exist!");
		}
	}
	
	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable int userId) {
		User findUser = userRepo.findById(userId).get();
		userRepo.deleteById(userId);
	}
	
	@PutMapping("/edit")
	public User editUser(@RequestBody User user) {
		User findUser = userRepo.findById(user.getId()).get();
		return userRepo.save(user);
	}
	
	//Forgot Password
	
	@PostMapping("/forgotpassword")
    @Transactional
    public User ResetPassword(@RequestBody User user){
        User findUser = userRepo.findByEmail(user.getEmail()).get();
        String message = "<h1>Link Recovery Password!</h1>\n ";
        message +="Silahkan klik <a href=\"http://localhost:3000/resetPassword/"+findUser.getId()+"/"+findUser.getVerifyToken()+"\">link</a> ini untuk reset ulang password anda";
        emailUtil.sendEmail(user.getEmail(), "Email Confirmation", message);
        return findUser;
    }
	// user_to_reset
	
	@PostMapping("/reset/{userId}/{userVerif}")
    public User getUserById(@PathVariable int userId, @PathVariable String userVerif){
//        System.out.println(user.getVerifyToken());
//        System.out.println(user.getId());

        User findUser = userRepo.findById(userId).get();
        findUser = userRepo.findByVerif(userVerif).get();
        return findUser;
    }
	
	// resetpassword
	
	@PutMapping("/resetpassword")
    public User userResetPassword(@RequestBody User user){
        User findUser = userRepo.findById(user.getId()).get();
        String encodedPassword = pwEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);
        return user;
    }
	
	@PostMapping("/changePassword")
    public User userChangePassword(@RequestBody User user, @RequestParam("oldPass") String oldPass,@RequestParam("newPass") String newPass){
        User findUser = userRepo.findById(user.getId()).get();
        System.out.println(oldPass);
        System.out.println(newPass);

        if (pwEncoder.matches(oldPass, findUser.getPassword())) {
            System.out.println("masuk");
            String newPassEncoded = pwEncoder.encode(newPass);
            findUser.setPassword(newPassEncoded);
            userRepo.save(findUser);
            return findUser;
        }
        throw new RuntimeException("Wrong old password!");
    }
}
