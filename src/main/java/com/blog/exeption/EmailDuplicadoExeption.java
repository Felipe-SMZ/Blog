package com.blog.exeption;

public class EmailDuplicadoExeption extends RuntimeException {

    public EmailDuplicadoExeption(String mensagem) {
        super(mensagem);
    }
}
