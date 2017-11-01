package com.eco.nlp.fasttext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.jfasttext.JFastText;

public class FastTextWrapper {
	
	private JFastText jft;
	
	private static FastTextWrapper instance;
	
	private FastTextWrapper() {
		jft = new JFastText();
		// Load model from file
		System.out.println("loading model...");
		jft.loadModel("/opt/fasttext/model/wiki.zh.bin");
		System.out.println("model loaded...");
	}
	
	public static FastTextWrapper getInstance() {
		if (instance == null) {
			instance = new FastTextWrapper();
		}
		
		return instance;
	}
	
	public List<Float> w2v(String word) {
		return jft.getVector(word);
	}
	
	public List<Float> sent2vec(String[] sentence) {
		float[] weights = new float[sentence.length];
		for (int w = 0; w < weights.length; w++) {
			weights[w] = 1.0f;
		}
		return this.avgw2v(sentence, weights);
	}
	
	public List<Float> sent2vec(String[] sentence, float[] weights) {
		return this.avgw2v(sentence, weights);
	}
	
	public List<Float> avgw2v(String[] sentence, float[] weights) {
		try {
			Float[] avg = null;
			for (int w = 0; w < sentence.length; w++) {
				String word = sentence[w];
				List<Float> vector = this.w2v(word);
				if (avg == null) {
					avg = new Float[vector.size()];
					for (int i = 0; i < avg.length; i++) {
						avg[i] = 0f;
					}
				}
				
				for (int i = 0; i < avg.length; i++) {
					avg[i] += vector.get(i) * weights[w];
				}
			}
			
			float sum = 0;
			for (int w = 0; w < weights.length; w++) {
				sum += weights[w];
			}
			
			for (int i = 0; i < avg.length; i++) {
				avg[i] = avg[i] / sum;
			}
			
			return Arrays.asList(avg);
		} catch (Exception e) {
			return new ArrayList<Float>();
		}
	}
	
	public static void main(String[] args) {
		
		FastTextWrapper ft = FastTextWrapper.getInstance();

		// Do label prediction
		String text = "hello";
		List<Float> vector = ft.w2v(text);
		System.out.println(String.join(",", vector.stream().map(o -> o.toString()).collect(Collectors.toList())));
	}
}
