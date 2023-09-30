package agileluvr.common.errors;

import agileluvr.common.errors.project.*;
import agileluvr.common.errors.user.NotAuthorizedError;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.errors.user.UserNotOwnerOfProjectError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String constraintViolationExceptionError(ConstraintViolationException error) { return error.getMessage(); }

    @ResponseBody
    @ExceptionHandler(UserNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundError(UserNotFoundError error) { return error.getMessage(); }

    @ResponseBody
    @ExceptionHandler(UserNotOwnerOfProjectError.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String userNotOwnerOfProjectError(UserNotOwnerOfProjectError error) { return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(NotAuthorizedError.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String notAuthorizedError(NotAuthorizedError error) { return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(AlreadyHasProjectError.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String alreadyHasProjectError(AlreadyHasProjectError error) { return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(InvalidTeamTypeError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidTeamTypeError(InvalidTeamTypeError error) { return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(NoTeamChosenError.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String noTeamChosenError(NoTeamChosenError error) { return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(ProjectDoesNotExistError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String projectDoesNotExistError(ProjectDoesNotExistError error){ return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(TeamFullError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String teamFullError(TeamFullError error){ return error.getMessage(); }
    @ResponseBody
    @ExceptionHandler(TeamSizeDiscrepancyError.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String teamSizeDiscrepancyError(TeamSizeDiscrepancyError error){ return error.getMessage(); }

    @ResponseBody
    @ExceptionHandler(TeamSizeOutOfBoundsError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String teamSizeOutOfBoundsError(TeamSizeOutOfBoundsError error){ return error.getMessage(); }

}
