package com.demo.springcustomizedstarterexample.utils.exceptions;

public class CustomAppException extends RuntimeException
{

	public CustomAppException() { }

	public CustomAppException(String message)
	{
		super(message);
	}

	public CustomAppException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CustomAppException(Throwable cause)
	{
		super(cause);
	}
}
