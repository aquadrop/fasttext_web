package com.eco.nlp.fasttext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

 import com.eco.nlp.fasttext.FastTextWrapper;

@SpringBootApplication
@EnableAutoConfiguration
@RestController
public class TokenizerApplicationController {
	FastTextWrapper core = FastTextWrapper.getInstance();
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
    String s2v(@RequestParam(value="q") String text, @RequestParam(value="w") String weight) throws JsonProcessingException {
		String[] sentence = text.split(",");
		float[] weights = null;
		if (weight != null && !StringUtils.isEmpty(weight)) {
			String[] weightStringList = weight.split(",");
			// order is assumed to be right
			if (weightStringList.length > 0 && weightStringList.length == sentence.length) {
				weights = new float[sentence.length];
				for (int i = 0; i < weights.length; i++) {
					weights[i] = Float.valueOf(weightStringList[i]);
				}
			}
		}
		List<Float> vector = null;
		if (weights == null) {
			vector = core.sent2vec(sentence);
		} else {
			vector = core.sent2vec(sentence, weights);
		}
        Map<String, List<Float>> result = new HashMap<String, List<Float>>();
        result.put("vector", vector);
        return JsonUtils.toJson(result);
    }

	@RequestMapping("/fasttext/maxsim")
    @ResponseBody
    String maxsim(@RequestParam(value="q1") String text1, @RequestParam(value="q2") String text2) throws JsonProcessingException {
		String[] sentence1 = text1.split(",");
//		System.out.println(text2);
		String[] sentence2 = text2.split("@@");   //q1和多个句子计算相似度
		List<Float> vector1 = null;
		List<Float> vector2 = null;
		double cossim = 0;
		double maxcossim = cossim;
		vector1= core.sent2vec(sentence1);
		Vector<String[]> strv= new Vector<String[]>(sentence2.length); 
		Vector<Double> cosv= new Vector<Double>(sentence2.length); 
		String simstring = null;
		for(int i=0 ; i < sentence2.length ;i++) {
			String[] sen = sentence2[i].split(",");
			strv.add(sen);
			vector2= core.sent2vec(sen);
			cossim = listcos(vector1,vector2);   //计算相似度
//			System.out.println(text1 + sentence2[i] + cossim);
			cosv.add(cossim);
			if(cossim > maxcossim) {
				maxcossim = cossim;
				simstring = sentence2[i];
			}
		}
		Map<String, String> result = new HashMap<String, String>();
		result.put("simstring",simstring);
		result.put("maxcossim",Double.toString(maxcossim));
		return JsonUtils.toJson(result);
	}
	
	@RequestMapping("/fasttext/sim")
    @ResponseBody
    double sim(@RequestParam(value="q1") String text1, @RequestParam(value="q2") String text2) throws JsonProcessingException {
		String[] sentence1 = text1.split(",");
		String[] sentence2 = text2.split(",");
		List<Float> vector1 = null;
		List<Float> vector2 = null;
		vector1= core.sent2vec(sentence1);
		vector2= core.sent2vec(sentence2);
		double cossim = listcos(vector1,vector2);
		return cossim;
	}
	
	//计算两个List<Float>的余弦相似度
	double listcos(List<Float> vector1,List<Float>vector2) {
		double ab = listmulti(vector1, vector2);
		double aa = listmulti(vector1, vector1);
		double bb = listmulti(vector2, vector2);
		return ab/(Math.sqrt(aa)*Math.sqrt(bb));
	}
	
	//计算两个list的乘积
	float listmulti( List<Float> vector1, List<Float>vector2) {
		float cos = 0;
		for(int i=0 ; i<vector1.size(); i++) {
			cos += vector1.get(i)*vector2.get(i);
		}
		return cos;
	}

	public static void main(String[] args) {
		SpringApplication.run(TokenizerApplicationController.class, args);
	}
}
