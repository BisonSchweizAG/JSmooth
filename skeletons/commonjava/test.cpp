
#include "JClassProxy.h"
#include "JMethodCaller.h"
#include "SunJVMDLL.h"
#include "SunJVMExe.h"
#include "Thread.h"

#include "JVMRegistryLookup.h"

void _debugOutput(const std::string& text)
{
  printf("%s\n", text.c_str());
}

void _debugWaitKey()
{
}

void thread_test(void* param)
{
  SunJVMDLL* jvm = (SunJVMDLL*)param;

  jclass c1 = jvm->findClass("java.lang.System");
  printf("class java.lang.System = %d\n", c1);

  jmethodID m1 = jvm->findMethod(c1, "currentTimeMillis", "()J", true);
  printf("method currentTimeMillis = %d\n", m1);

  jvalue v[0];

  for(int i=0; i<100; i++)
    {
      long t = jvm->invokeLongStatic(c1, m1, v);
      printf("%d... current millis : %ld\n", i, t);
      Thread::sleep(1000);
      fflush(stdout);
    }  
}

int test_dll()
{
  SunJVMDLL jvm("c:\\Program Files\\Java\\jdk1.5.0_11\\jre\\bin\\client\\jvm.dll", Version("1.2"));
  //SunJVMDLL jvm("c:\\Program Files\\JavaSoft\\JRE\\1.1\\bin\\javai.dll", Version("1.1"));
  jvm.addPathElement("..\\..\\sample\\sample.jar");
  jvm.setMaxHeap(6231616);
  jvm.setInitialHeap(6231616);

  jvm.instanciate();

  JClassProxy disp(&jvm, "JSmoothPropertiesDisplayer");
  jstring emptystr = jvm.newUTFString(std::string(""));  
  jobjectArray mainargs = jvm.newObjectArray(0, "java.lang.String", emptystr);
  printf("arguments array = %d\n", mainargs);
  jvalue ma[1];
  ma[0].l = mainargs;
  disp.invokeStatic(std::string("void main(java.lang.String[] args)"), ma);

  jvalue frameargs[1];

  JClassProxy frameproxy(&jvm, "javax.swing.JFrame");
  jstring str = jvm.newUTFString(std::string("Ceci est un test!"));
  jobject frame2 = frameproxy.newInstance("void javax.swing.JFrame(java.lang.String str)", 
					  JArgs((jobject)str));
  printf("FRAME=%d\n", frame2);

  frameproxy.invoke(frame2, "void setVisible(boolean b)", JArgs((bool)true));

  Thread t1;
  t1.start(thread_test, &jvm);
  t1.sleep(6000);

  Thread t2;
  t2.start(thread_test,&jvm);
  
  t1.join();
  t2.join();
}

int main()
{
  std::string jrehome = "c:\\Program Files\\JavaSoft\\JRE\\1.1";
  //  jrehome = "c:\\Program Files\\JavaSoft\\JRE\\1.3.1_02";

  SunJVMExe jre(jrehome);
  jre.addPathElement(".");
  jre.addPathElement("..\\..\\sample\\sample.jar");
  jre.setMaxHeap(6231616);
  jre.setInitialHeap(6231616);

  //SunJVMDLL jvm("c:\\Program Files\\JavaSoft\\JRE\\1.1\\bin\\javai.dll", Version("1.1"));
  
  DEBUG( "FOUND: " + jre.lookUpExecutable(false) );

  DEBUG("Version...: " + jre.guessVersion().toString());
  DEBUG("PARENT: " + FileUtils::getParent(jrehome));

  DEBUG("CP1= " + jre.getClassPath(false));
  DEBUG("CP2= " + jre.getClassPath(true));
  
  jre.run("maclasse", false);

  vector<SunJVMLauncher> launchers = JVMRegistryLookup::lookupJVM();
  for (int i=0; i<launchers.size(); i++)
    {
      DEBUG(launchers[i].toString());
    }

}

