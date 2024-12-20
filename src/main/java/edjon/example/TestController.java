package edjon.example;


import edjon.example.entity.Task;
import edjon.example.entity.TaskDto;
import edjon.example.entity.TaskRepository;
import edjon.example.exception.RunTimeException1;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class TestController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ModelMapper simpleModelMapper;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    @PutMapping(value = "/random/{id}")
    public ResponseEntity<TaskDto> errorUpdateAnyway(@PathVariable("id") Long id,
                                                     @RequestBody TaskDto taskDto){

        if(id.equals(1l))
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body( taskDto);

        return ResponseEntity.status(HttpStatus.OK).body(taskDto);

       // curl -i -X PUT -H 'Content-Type: application/json' -d '{"id": "1", "name": "changedTo1", "description": "the first task"}' http://localhost:8080/random/1
        // error still has entity json message,  need use base DTO with reason message if we also want to return reason/error message when error happens.
    }


    @PutMapping(value = "/ex/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto noCustomExpAndAdviceAndHandler(@PathVariable("id") Long id, @RequestBody TaskDto taskDto){
        if(id.equals(1l)){

            // reason will be shown if not set server.error.include-message=always
            // however if it is set , it's not secure.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't query id 1");
        }
        Task task = taskRepository.findById(id.longValue());

        TaskDto taskDto1 = new TaskDto();
        taskDto1.setName(task.getName());
        return taskDto1;

        //curl -i -X PUT -H 'Content-Type: application/json' -d '{"id": "1", "name": "changedTo1", "description": "the first task"}' http://localhost:8080/ex/1
        //error doesn't have entity json message
    }



    @PutMapping(value = "/put/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto updateTask(@PathVariable("id") Long id, @RequestBody TaskDto taskDto){

        Optional<Task> taskOptional = taskRepository.findById(id);


        if(taskOptional.isEmpty() || taskDto.getName() == null
            || taskDto.getName().isEmpty() || taskDto.getDescription() == null
            || taskDto.getDescription().isEmpty())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "please check name and description");


        Task task = taskOptional.get();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());

        taskRepository.save(task);

        return taskDto;


    }


    @PutMapping(value = "/advice/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto usingAdvice(@PathVariable("id") Long id, @RequestBody TaskDto taskDto){
       throw new RunTimeException1("internal error happens");
    }

    @GetMapping(value = "/task/{id}")
    @ResponseBody
    public TaskDto getTask(@PathVariable("id") Long id) {
        TaskDto taskDto = new TaskDto();

        Task task = taskRepository.findById(id.longValue());

        if(task != null){
            taskDto.setId(task.getId());
            taskDto.setName(task.getName());
            taskDto.setDescription(task.getDescription());
        }
        return  taskDto;
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TaskDto createPost(@RequestBody TaskDto taskDto) {
        Task task = new Task();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setStatus(1);
        taskRepository.save(task);

        return  taskDto;
    }
}
