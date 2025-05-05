package org.example.scrd.exception;

public class PartyClosedException extends IllegalStateException {

    private static final String MESSAGE = "모집이 마감된 일행입니다.";

    public PartyClosedException() {
        super(MESSAGE);
    }
}
