package de.l3s.distantsupervision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_ACE2004;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_Derczynski;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_KORE50;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_N3RSS500;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_Spotlight;
import de.l3s.loaders.DataLoaders_N3Reuters128;
import de.l3s.loaders.DataLoaders_WP;
import meka.classifiers.multilabel.BR;
import meka.classifiers.multitarget.MultiTargetClassifier;
import meka.core.MLUtils;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.OneClassClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Remove;

public class DistantSuperTraining {

	
	public DistantSuperTraining() {
		super();
	}

	
	
	public static void main(String[] args) throws Exception{
		String corpus = "ace2004";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "derczynkski";
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
//		String corpus = "kore50";
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "N3News100";   			// 	GERMAN NEWS
//		String corpus = "N3Reuters128";
//		String corpus = "N3RSS500";
//	    String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "spotlight" ;
// 		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
		DataLoaders d = new DataLoaders();
		if(corpus.equalsIgnoreCase("ace2004")){
			d = DataLoaders_ACE2004.getInstance();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			d = DataLoaders_AQUAINT.getInstance();
		}
		if(corpus.equalsIgnoreCase("conll")){
			d = DataLoaders_CONLL.getInstance();
		}
		if(corpus.equalsIgnoreCase("derczynkski")){
			d = DataLoaders_Derczynski.getInstance();
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			d = DataLoaders_GERDAQ.getInstance();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			d = DataLoaders_IITB.getInstance();
		}
		if(corpus.equalsIgnoreCase("kore50")){
			d = DataLoaders_KORE50.getInstance();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}
		if(corpus.equalsIgnoreCase("N3Reuters128")){
			d = DataLoaders_N3Reuters128.getInstance();
		}
		if(corpus.equalsIgnoreCase("N3RSS500")){
			d = DataLoaders_N3RSS500.getInstance();
		}
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("spotlight")){
			d = DataLoaders_Spotlight.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}
		String mode = "R1";
		
//		double[] p = new double[]{10.0,20.0,40.0,80.0,100.0};
 		double[] p = new double[]{1.0,5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0};
		for(double dd : p){
			
//			train_and_Predict_MultiClass(corpus,dd);		
 			train_and_Predict_Multilabel(corpus,dd);
 			trainOneClassClassifiers(corpus,mode,dd);
		}



		
	}
	
	
	
	
	/**
	 *
	 * @param corpus
	 * @param mode
	 * @param feat
	 * @param percent
	 * @throws Exception
	 */
	private static void trainOneClassClassifiers(String corpus, String mode, double percent) throws Exception {
		OutputStreamWriter outPredictions = null;
//		OutputStreamWriter classOut = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/classifiers."+feat+".txt"), StandardCharsets.UTF_8);

		//filter to remove unused columns from training data
		Remove rm = new Remove();
		rm.setAttributeIndicesArray(new int[]{0,1,2,3});
		
		//CLASSIFIERS - Ambiverse data*****************************************************************************************	/
		Instances train_Amb_data = DataSource.read("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+"."+percent+".train.R1.Amb.ORG.arff");
		train_Amb_data.setClassIndex(train_Amb_data.numAttributes() - 1);
		
		Instances unlabeled_test_Amb_data = DataSource.read("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.R1.Amb.arff");
//		Instances labeled_test_Amb_data = DataSource.read("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".test.lab.R1.Amb.arff");
				
		unlabeled_test_Amb_data.setClassIndex(unlabeled_test_Amb_data.numAttributes() - 1);
//		labeled_test_Amb_data.setClassIndex(labeled_test_Amb_data.numAttributes() - 1);
			
//		Instances labeled_TEST_Amb_data = new Instances(labeled_test_Amb_data);
		Instances unlabeled_TEST_Amb_data  = new Instances(unlabeled_test_Amb_data);
		
		Instances labeled_Amb_data = new Instances(unlabeled_test_Amb_data);
		rm.setInputFormat(train_Amb_data);
//		
		rm.setInputFormat(train_Amb_data);
		Instances newtrain_Amb_data = Filter.useFilter(train_Amb_data, rm);
//		Instances newtrain_Amb_data = Filter.useFilter(train_Amb_data, rm);  
//		
		rm.setInputFormat(unlabeled_test_Amb_data);
//		unlabeled_test_Amb_data = Filter.useFilter(unlabeled_test_Amb_data, rm);  	
		unlabeled_test_Amb_data = Filter.useFilter(unlabeled_test_Amb_data, rm); 
	
//		rm.setInputFormat(labeled_test_Amb_data);
//		labeled_test_Amb_data = Filter.useFilter(labeled_test_Amb_data, rm);
//		labeled_test_Amb_data = Filter.useFilter(labeled_test_Amb_data, rm);

//		
//		Instances labeled_TEST_Amb_data = new Instances(labeled_test_Amb_data);
		
		/*** One Class Classifier WEKA ***/
		OneClassClassifier occ = new OneClassClassifier();
		
		System.out.println("...Training Ambiverse OneClass classifier for ["+corpus+"]."+"\n");
		occ.setTargetClassLabel("1");
		occ.buildClassifier(newtrain_Amb_data);
		
		outPredictions = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/models"+mode+"/dataset.oneclass."+corpus+"."+mode+".occ.Amb."+percent+".predictions"), StandardCharsets.UTF_8);
		// Saving the predictions
		for (int i = 0; i < unlabeled_test_Amb_data.numInstances(); i++) {
			double clsLabel = occ.classifyInstance(unlabeled_test_Amb_data.instance(i));
//			System.out.println(unlabeled_test_Amb_data.instance(i) + ":" + clsLabel);
			labeled_Amb_data.instance(i).setClassValue(clsLabel);
			unlabeled_TEST_Amb_data.instance(i).setClassValue(clsLabel);
//			System.out.println(labeled_Amb_data.instance(i)  + ":" + clsLabel);
			outPredictions.write(unlabeled_TEST_Amb_data.instance(i)+"\n");
		}
		outPredictions.flush();
		outPredictions.close();

		Evaluation eval = new Evaluation(newtrain_Amb_data);
		eval.crossValidateModel(occ, newtrain_Amb_data, 10, new Random(1));
		System.out.println();
		System.out.println(" === Ambiverse 10-fold cross validation on training set. === OCC === "+percent+"\n");
        System.out.println();
        System.out.println(eval.toSummaryString()+"\n");
    	System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString("Confusion matrix:"));
//		System.out.println(eval.toCumulativeMarginDistributionString());
        System.out.println();
        
//        
//        eval = new Evaluation(newtrain_Amb_data);
//        eval.evaluateModel(occ, labeled_test_Amb_data);
//        System.out.println();
//        System.out.println(" === Ambiverse Supplied test set evaluation. === OCC === @"+percent+"\n");
//        System.out.println();
//        System.out.println(eval.toSummaryString()+"\n");
//     	System.out.println(eval.toClassDetailsString());
//        System.out.println(eval.toMatrixString("Confusion matrix:"));
//		System.out.println(eval.toCumulativeMarginDistributionString());
        System.out.println();
//		//END OF CLASSIFIERS - Ambiverse data*****************************************************************************************	//
		
		
		//CLASSIFIERS - Babelfy data*****************************************************************************************	//
		Instances train_Bab_data = DataSource.read("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+"."+percent+".train.R1.Bab.ORG.arff");
		train_Bab_data.setClassIndex(train_Bab_data.numAttributes() - 1);
		
		Instances unlabeled_test_Bab_data = DataSource.read("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.R1.Bab.arff");
//		Instances labeled_test_Bab_data = DataSource.read("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".test.lab.R1.Bab.arff");
				
		unlabeled_test_Bab_data.setClassIndex(unlabeled_test_Bab_data.numAttributes() - 1);
//		labeled_test_Bab_data.setClassIndex(labeled_test_Bab_data.numAttributes() - 1);
				
//		Instances labeled_TEST_Bab_data = new Instances(labeled_test_Bab_data);
		Instances unlabeled_TEST_Bab_data  = new Instances(unlabeled_test_Bab_data);
		
		Instances labeled_Bab_data = new Instances(unlabeled_test_Bab_data);
		rm.setInputFormat(train_Bab_data);
		Instances newtrain_Bab_data = Filter.useFilter(train_Bab_data, rm);  
		
		rm.setInputFormat(unlabeled_test_Bab_data);
		unlabeled_test_Bab_data = Filter.useFilter(unlabeled_test_Bab_data, rm);  		
		
//		rm.setInputFormat(labeled_test_Bab_data);
//		labeled_test_Bab_data = Filter.useFilter(labeled_test_Bab_data, rm);
		
		/*** One Class Classifier WEKA ***/
		occ = new OneClassClassifier();

		System.out.println("...Training Babelfy OneClass classifier for ["+corpus+"]."+"\n");

		occ.setTargetClassLabel("1");
		occ.buildClassifier(newtrain_Bab_data);
		
		outPredictions = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/models"+mode+"/dataset.oneclass."+corpus+"."+mode+".occ.Bab."+percent+".predictions"), StandardCharsets.UTF_8);
		// Saving the predictions
		for (int i = 0; i < unlabeled_test_Bab_data.numInstances(); i++) {
			double clsLabel = occ.classifyInstance(unlabeled_test_Bab_data.instance(i));
			labeled_Bab_data.instance(i).setClassValue(clsLabel);
			unlabeled_TEST_Bab_data.instance(i).setClassValue(clsLabel);
			outPredictions.write(unlabeled_TEST_Bab_data.instance(i)+"\n");
		}
		outPredictions.flush();
		outPredictions.close();

		//END OF CLASSIFIERS - Babelfy data*****************************************************************************************	//		

		//CLASSIFIERS - Tagme data*****************************************************************************************	//
		Instances train_Tag_data = DataSource.read("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+"."+percent+".train.R1.Tag.ORG.arff");
		train_Tag_data.setClassIndex(train_Tag_data.numAttributes() - 1);
		
		Instances unlabeled_test_Tag_data = DataSource.read("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.R1.Tag.arff");
//		Instances labeled_test_Tag_data = DataSource.read("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".test.lab.R1.Tag.arff");
				
		unlabeled_test_Tag_data.setClassIndex(unlabeled_test_Tag_data.numAttributes() - 1);
//		labeled_test_Tag_data.setClassIndex(labeled_test_Tag_data.numAttributes() - 1);
		
//		Instances labeled_TEST_Tag_data = new Instances(labeled_test_Tag_data);
		Instances unlabeled_TEST_Tag_data  = new Instances(unlabeled_test_Tag_data);
		
		
		Instances labeled_Tag_data = new Instances(unlabeled_test_Tag_data);
		rm.setInputFormat(train_Tag_data);
		Instances newtrain_Tag_data = Filter.useFilter(train_Tag_data, rm);  

		rm.setInputFormat(unlabeled_test_Tag_data);
		unlabeled_test_Tag_data = Filter.useFilter(unlabeled_test_Tag_data, rm);  		
		
//		rm.setInputFormat(labeled_test_Tag_data);
//		labeled_test_Tag_data = Filter.useFilter(labeled_test_Tag_data, rm);
	
		/*** One Class Classifier WEKA ***/

		occ = new OneClassClassifier();
		
		System.out.println("...Training Tagme OneClass classifier for ["+corpus+"]."+"\n");

		occ.setTargetClassLabel("1");
		occ.buildClassifier(newtrain_Tag_data);
		
		outPredictions = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/models"+mode+"/dataset.oneclass."+corpus+"."+mode+".occ.Tag."+percent+".predictions"), StandardCharsets.UTF_8);
		// Saving the predictions
		for (int i = 0; i < unlabeled_test_Tag_data.numInstances(); i++) {
			double clsLabel = occ.classifyInstance(unlabeled_test_Tag_data.instance(i));
			labeled_Tag_data.instance(i).setClassValue(clsLabel);
			unlabeled_TEST_Tag_data.instance(i).setClassValue(clsLabel);
			outPredictions.write(unlabeled_TEST_Tag_data.instance(i)+"\n");
		}
		outPredictions.flush();
		outPredictions.close();
		//END OF CLASSIFIERS - Tagme data*****************************************************************************************	//
	}
	
	/**
	 *
	 * @param corpus
	 * @param f
	 * @throws Exception
	 */
	private static void train_and_Predict_Multilabel(String corpus, double percent) throws Exception {
	
		RandomForest randomForest = new RandomForest();
		randomForest.setBagSizePercent(100);
		randomForest.setComputeAttributeImportance(true);
		randomForest.setNumFeatures(0);
		randomForest.setMaxDepth(0);
		randomForest.setNumDecimalPlaces(2);
		randomForest.setNumExecutionSlots(15);
		randomForest.setNumIterations(100);
		randomForest.setSeed(1);
//		//String Command = "java -cp /home/joao/meka-release-1.9.3-SNAPSHOT/lib/* meka.classifiers.multilabel.BR -t /home/joao/meka-release-1.9.3-SNAPSHOT/data/Music.arff -x 10 -verbosity 8 -predictions /dev/null -W weka.classifiers.trees.RandomForest -output-debug-info -- -I 100 -num-slots 15 -S 1";
		BR classifier = new BR();
	
	    Instances training_data = DataSource.read("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".train."+percent+".arff");
	    											
	    MLUtils.prepareData(training_data);
	    classifier.setClassifier(randomForest);

	    Instances test_data = DataSource.read("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".test.arff");
	    Instances test_dataORG = DataSource.read("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".test.ORG.arff");
    

	    MLUtils.prepareData(test_data);

	    // compatible?
	    String msg = training_data.equalHeadersMsg(test_data);
	    if (msg != null)
	      throw new IllegalStateException(msg);
	    
	    classifier.buildClassifier(training_data);
	    System.out.println("...Training classifier for multilabel mode @"+percent+"\n");

	    //@@@ Output predictions over the test set.
	    OutputStreamWriter outPred = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions"), StandardCharsets.UTF_8);
		
	    Instances predicted = new Instances(test_data, 0);
	    for (int i = 0; i < test_data.numInstances(); i++) {
	    	double pred[] = classifier.distributionForInstance(test_data.instance(i));
	    	double[] dist = classifier.distributionForInstance(test_data.instance(i));
	    	
	    	if (classifier instanceof MultiTargetClassifier){
	    		pred = Arrays.copyOfRange(pred, test_data.classIndex(), test_data.classIndex()*2);
	    	}
	    	Instance predInst = (Instance) test_data.instance(i).copy();
	    	for (int j = 0; j < pred.length; j++){
				predInst.setValue(j, Math.round(pred[j])); // ML have probabilities; MT have discrete label indices
	    	}
	    	//3 last ones are the confidence
	    	outPred.write(test_dataORG.instance(i)+","+predInst.value(0)+","+predInst.value(1)+","+ predInst.value(2)+","+ weka.core.Utils.arrayToString(dist)+"\n");
			predicted.add(predInst);
	    	
	    }
	    outPred.flush();
		outPred.close();
		System.out.println("Confidence saved successfully.");
		
		AbstractFileSaver saver = ConverterUtils.getSaverForFile(new File("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions.arff"));
		if (saver == null) {
			System.err.println("Failed to determine saver ");
			saver = new ArffSaver();
		}
		saver.setFile(new File("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions.arff"));
		saver.setInstances(predicted);
		saver.writeBatch();
	}
	
	
	
	
	/**
	 * 
	 * @param corpus
	 * @throws Exception
	 */
	private static void train_and_Predict_MultiClass(String corpus,double percent) throws Exception {
		
		BufferedReader reader =  new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".train."+percent+".ORG.arff"),StandardCharsets.UTF_8));
		
		ArffReader arff = new ArffReader(reader);
		Instances train_data = arff.getData();
		// setting the class label index for the training data
		train_data.setClassIndex(train_data.numAttributes() - 1);
		reader =  new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".test.ORG.arff"),StandardCharsets.UTF_8));

		arff = new ArffReader(reader);
		Instances unlabeled = arff.getData();
		
//		ArffLoader loader = new ArffLoader();
//		loader.setFile(new File("./resources/"+corpus+"/dataset.multiclass."+corpus+".test.ORG.arff"));
//		Instances unlabeled = loader.getDataSet();
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
		// create copy
		Instances labeled = new Instances(unlabeled);
		// create copy
		Instances unlabeled_TEST = new Instances(unlabeled);


//		int seed = 1;
		//filter to remove unused columns from training data
		Remove rm = new Remove();
		rm.setAttributeIndicesArray(new int[]{0,1,2,3});
		
		rm.setInputFormat(train_data);
	    Instances newTrain = Filter.useFilter(train_data, rm); 
	    
	    rm.setInputFormat(unlabeled);
	    unlabeled = Filter.useFilter(unlabeled, rm); 
	    
	    String msg = newTrain.equalHeadersMsg(unlabeled);
	    if (msg != null){
	      throw new IllegalStateException(msg);
	    }

		SpreadSubsample ff = new SpreadSubsample(); // 0 = no maximum spread,  1 = uniform distribution,
		ff.setDistributionSpread(0);
		ff.setMaxCount(0);
		ff.setRandomSeed(1);

		// meta-classifier
		FilteredClassifier fc = new FilteredClassifier();
				
		SMO smo = new SMO();
		
		fc.setFilter(ff);
		fc.setClassifier(smo);
		
		System.out.println("...Training classifier for multiclass mode. @"+percent+"\n");
		fc.buildClassifier(newTrain);

		Evaluation eval = new Evaluation(newTrain);
		eval.crossValidateModel(smo, newTrain, 10, new Random(1));
	
        System.out.println();
		System.out.println(" === 10-fold cross validation on training set. ===");
        System.out.println();

		System.out.println(eval.toMatrixString("Confusion matrix:"));
		System.out.println((eval.toClassDetailsString()));
		System.out.println(eval.toCumulativeMarginDistributionString());
		System.out.println(eval.toSummaryString());
////		
    	OutputStreamWriter outPred = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+"."+percent+".pred"), StandardCharsets.UTF_8);
																 
		System.out.println("Saving predictions @"+percent+"\n");
		 // label instances
		for (int i = 0; i < unlabeled.numInstances(); i++) {
			double clsLabel = smo.classifyInstance(unlabeled.instance(i));
//			System.out.println(unlabeled.instance(i) + "\t" + clsLabel);
			unlabeled_TEST.instance(i).setClassValue(clsLabel);
//			labeled.instance(i).setClassValue(clsLabel);
			outPred.write(unlabeled_TEST.instance(i)+"\n");
//			// Get the prediction probability distribution.
//	        double[] predictionDistribution =       smo.distributionForInstance(unlabeled.instance(i));
		}
		
		outPred.flush();
		outPred.close();
		System.out.println("Predictions saved successfully @"+percent+"\n");
		System.out.println();
	}
	
	
	
	
}
