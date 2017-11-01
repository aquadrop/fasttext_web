package com.eco.nlp.fasttext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	
	public static ObjectMapper mapper = new ObjectMapper();
	
	public static<T> T toObject(String json) {

		try {
			// convert JSON string to Map
			@SuppressWarnings("unchecked")
			T t = (T)mapper.readValue(json, Object.class);

			return t;

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String toJson(Object object) throws JsonProcessingException {
		String json = mapper.writeValueAsString(object);
		return json;
	}
}
