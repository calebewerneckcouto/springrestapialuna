package curso.api.rest;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {

    /**
     * Tratamento de exceções genéricas
     */
    @ExceptionHandler({ Exception.class, RuntimeException.class, Throwable.class })
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {

        String msg = "Erro inesperado. Por favor, entre em contato com o suporte.";

        if (ex instanceof MethodArgumentNotValidException) {
            List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError objectError : list) {
                errorMsg.append(objectError.getDefaultMessage()).append("\n");
            }
            msg = errorMsg.toString().trim();
        } else if (ex.getMessage() != null) {
            msg = ex.getMessage();
        }

        ObjetoError objetoError = new ObjetoError();
        objetoError.setError(msg);
        objetoError.setCode(status.value() + " ==> " + status.getReasonPhrase());

        return new ResponseEntity<>(objetoError, headers, status);
    }

    /**
     * Tratamento de erros de banco de dados (violação de integridade de dados)
     */
    @ExceptionHandler({ DataIntegrityViolationException.class, PSQLException.class, SQLException.class })
    protected ResponseEntity<Object> handleExceptionDataIntegrity(Exception ex) {

        String msg = "Erro de integridade dos dados. Por favor, verifique os campos enviados.";

        // Acessa a causa para obter a mensagem mais específica
        Throwable cause = ex.getCause();
        if (cause != null && cause.getCause() != null && cause.getCause().getMessage() != null) {
            msg = cause.getCause().getMessage();
        } else if (ex.getMessage() != null) {
            msg = ex.getMessage();
        }

        ObjetoError objetoError = new ObjetoError();
        objetoError.setError(msg);
        objetoError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value() + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return new ResponseEntity<>(objetoError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Tratamento de erros de violação de restrição (ConstraintViolationException)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationExceptions(ConstraintViolationException ex) {
        // Coleta todas as mensagens de violação de restrição
        List<String> errorMessages = ex.getConstraintViolations()
                                        .stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.toList());

        // Retorna as mensagens de erro com status 400 (Bad Request)
        ObjetoError objetoError = new ObjetoError();
        objetoError.setError(String.join("\n", errorMessages));
        objetoError.setCode(HttpStatus.BAD_REQUEST.value() + " ==> " + HttpStatus.BAD_REQUEST.getReasonPhrase());

        return new ResponseEntity<>(objetoError, HttpStatus.BAD_REQUEST);
    }
}
