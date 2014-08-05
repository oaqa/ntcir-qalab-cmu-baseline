package edu.cmu.lti.ntcir.qalab.utils;

import edu.cmu.lti.ntcir.qalab.types.TestDocument;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Utils {

  public static <T extends TOP> ArrayList<T> fromFSListToCollection(FSList list,
          Class<T> classType) {

    if (list == null) {
      return new ArrayList<T>();
    }

    Collection<T> myCollection = JCasUtil.select(list, classType);
    return new ArrayList<T>(myCollection);
  }

  public static <T extends Annotation> FSList fromCollectionToFSList(JCas aJCas,
          Collection<T> aCollection) {
    if (aCollection.size() == 0) {
      return new EmptyFSList(aJCas);
    }

    NonEmptyFSList head = new NonEmptyFSList(aJCas);
    NonEmptyFSList list = head;
    Iterator<T> i = aCollection.iterator();
    while (i.hasNext()) {
      head.setHead(i.next());
      if (i.hasNext()) {
        head.setTail(new NonEmptyFSList(aJCas));
        head = (NonEmptyFSList) head.getTail();
      } else {
        head.setTail(new EmptyFSList(aJCas));
      }
    }

    return list;
  }

  public static TestDocument getTestDocumentFromCAS(JCas jCas) {
    FSIterator it = jCas.getAnnotationIndex(TestDocument.type).iterator();
    TestDocument srcDoc = null;
    if (it.hasNext()) {
      srcDoc = (TestDocument) it.next();
    }
    return srcDoc;
  }

}
