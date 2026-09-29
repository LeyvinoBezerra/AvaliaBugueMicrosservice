package br.edu.ufersa.pw.todo.buggytrip.api.exceptions;

public class ConflictException extends DomainException {
    public ConflictException(String m) {
        super(m);
    }
}
