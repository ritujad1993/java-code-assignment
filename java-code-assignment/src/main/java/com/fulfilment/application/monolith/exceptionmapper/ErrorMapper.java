package com.fulfilment.application.monolith.exceptionmapper;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

	@Inject
	ObjectMapper objectMapper;

	private static final Logger LOGGER = Logger.getLogger(ErrorMapper.class.getName());

	@Override
	public Response toResponse(Exception exception) {
		LOGGER.error("Failed to handle request", exception);

		int code = 500;
		if (exception instanceof WebApplicationException) {
			code = ((WebApplicationException) exception).getResponse().getStatus();
		}

		ObjectNode exceptionJson = objectMapper.createObjectNode();
		exceptionJson.put("exceptionType", exception.getClass().getName());
		exceptionJson.put("code", code);

		if (exception.getMessage() != null) {
			exceptionJson.put("error", exception.getMessage());
		}

		return Response.status(code).entity(exceptionJson).build();
	}
}
