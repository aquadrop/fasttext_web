package com.eco.nlp.fasttext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.jfasttext.JFastText;

public class FastText {
	
	private JFastText jft;
	
	private static FastText instance;
	
	private FastText() {
		jft = new JFastText();
		// Load model from file
		System.out.println("loading model...");
		jft.loadModel("/opt/fasttext/model/wiki.zh.bin");
		System.out.println("model loaded...");
	}
	
	public static FastText getInstance() {
		if (instance == null) {
			instance = new FastText();
		}
		
		return instance;
	}
	
	public List<Float> w2v(String word) {
		return jft.getVector(word);
	}
	
	public List<Float> sent2vec(String[] sentence) {
		return this.avgw2v(sentence);
	}
	
	public List<Float> avgw2v(String[] sentence) {
		try {
			Float[] avg = null;
			for (String word : sentence) {
				List<Float> vector = this.w2v(word);
				if (avg == null) {
					avg = new Float[vector.size()];
					for (int i = 0; i < avg.length; i++) {
						avg[i] = 0f;
					}
				}
				
				for (int i = 0; i < avg.length; i++) {
					avg[i] += vector.get(i);
				}
			}
			
			for (int i = 0; i < avg.length; i++) {
				avg[i] = avg[i] / sentence.length;
			}
			
			return Arrays.asList(avg);
		} catch (Exception e) {
			return new ArrayList<Float>();
		}
	}
	
	public static void main(String[] args) {
		
		FastText ft = FastText.getInstance();

		// Do label prediction
		String text = "hello";
		List<Float> vector = ft.w2v(text);
		System.out.println(String.join(",", vector.stream().map(o -> o.toString()).collect(Collectors.toList())));
	}
}
