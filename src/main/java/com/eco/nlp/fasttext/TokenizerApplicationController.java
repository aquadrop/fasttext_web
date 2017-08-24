package com.eco.nlp.fasttext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;


@SpringBootApplication
@EnableAutoConfiguration
@RestController
public class TokenizerApplicationController {
	
	FastText core = FastText.getInstance();
	
	@RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
	
	@RequestMapping("/fasttext/w2v")
    @ResponseBody
    String w2v(@RequestParam(value="q") String text) throws JsonProcessingException {
		List<Float> vector = core.w2v(text);
		Map<String, List<Float>> result = new HashMap<String, List<Float>>();
        result.put("vector", vector);
        return JsonUtils.toJson(result);
    }
	
	@RequestMapping("/fasttext/s2v")
    @ResponseBody
    String s2v(@RequestParam(value="q") String text) throws JsonProcessingException {
		String[] sentence = text.split(",");
        List<Float> vector = core.sent2vec(sentence);
        Map<String, List<Float>> result = new HashMap<String, List<Float>>();
        result.put("vector", vector);
        return JsonUtils.toJson(result);
    }

	public static void main(String[] args) {
		SpringApplication.run(TokenizerApplicationController.class, args);
	}
}
