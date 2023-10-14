package br.com.dhweb.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dhweb.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        //Fazendo casting de String para UUID.
        taskModel.setIdUser((UUID)request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt())){
            return ResponseEntity.status(400).body("A data de início deve ser maior que a data atual.");    
        }
        if(currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(400).body("A data final deve ser maior ou igual que a data atual.");    
        }
        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(400).body("A data final deve ser maior ou igual que a data de início.");    
        }
        
        var task = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);

    }
    
    @GetMapping("/")
    public ResponseEntity list(HttpServletRequest request){
        
        var idUser = (UUID)request.getAttribute("idUser");
        List<TaskModel> listagem = this.taskRepository.findByIdUser(idUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(listagem);

    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        
        var idUser = (UUID)request.getAttribute("idUser");

        var task = this.taskRepository.findById((UUID)id).orElse(null); 
        
        if(task == null){
            return ResponseEntity.status(400).body("Tarefa não encontrada na base de dados para o id "+id+".");
        }

        if(!task.getIdUser().equals(idUser)){ 
            return ResponseEntity.status(401).body("Você não pode editar a tarefa de outro usuário.");
        }
        
        Utils.copyNonNullProperties(taskModel, task);
        
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.status(200).body(taskUpdated);
    
    }

}
