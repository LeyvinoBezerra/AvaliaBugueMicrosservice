package br.edu.ufersa.pw.todo.buggytrip.api.exceptions;

public class NotFoundException extends DomainException {
    public NotFoundException(String m) {
        super(m);
    }
}
