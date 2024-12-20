package edjon.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RunTimeException1 extends RuntimeException{

    public RunTimeException1(String message){
        super(message);
    }
}
