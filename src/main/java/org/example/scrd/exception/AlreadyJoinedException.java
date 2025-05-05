package org.example.scrd.exception;


public class AlreadyJoinedException extends IllegalStateException {

    private static final String MESSAGE = "이미 신청한 일행입니다.";
    public AlreadyJoinedException()  {
        super(MESSAGE);
    }
}
