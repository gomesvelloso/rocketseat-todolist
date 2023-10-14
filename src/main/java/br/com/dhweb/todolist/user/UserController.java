package br.com.dhweb.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    //Dados vem no body da requisição.
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){

        //Verificar se já existe o username
        var user = this.userRepository.findByUsername(userModel.getUsername());
        
        if(user != null){
            //Usuário existe.
            System.out.println("Usuário já existe na base de dados");
            //Menssagem de erro e o status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe na base de dados.");
        }

        var passwordHashred = BCrypt.withDefaults().
        hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashred);

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }
    
}
