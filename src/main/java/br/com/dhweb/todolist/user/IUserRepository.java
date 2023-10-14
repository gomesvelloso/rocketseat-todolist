package br.com.dhweb.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

//<Qual a classe que o repositório representa, qual Tipo de Id que a entidade tem> 
public interface IUserRepository extends JpaRepository<UserModel, UUID> {

    //O findByUsername vai retornar um objeto de userModel
    UserModel findByUsername(String userName);
    
}
