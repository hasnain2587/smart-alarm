package net.xcodersteam.lemoprint.api.web;


import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xcodersteam.lemoprint.api.Globals;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.janino.ClassBodyEvaluator;

/** Main class of LWJDWPF - Light Weight Java Dynamic Web Pages Framework
 * Created by semoro on 26.04.15.
 */
public class PageGenProcessor {
	private static final Log log = LogFactory.getLog(PageGenProcessor.class);
	private static final String prefixE = "<?j";
	private static final String prefixI = "<?j=";
    private static final String postfix = "?>";
    private static final Pattern pattern = Pattern.compile(Pattern.quote(prefixE)+"[<>\\d\\w\\s()?.,\"+=;\\\\*{}/\\-\\[\\]@\\n\\r]+"+Pattern.quote(postfix));
    private static final Pattern iPattern = Pattern.compile("import\\s+([ a-zA-Z.*]+);");
    
    String page = "";
    EchoClass obj;
    Method method;
    Class[] types;
    String[] params;
    public File on;

    private void collectEval(String e,StringBuilder code,int codeBlock,List<String> imports) {
    	Matcher m=iPattern.matcher(e);
    	while(m.find()){
    		imports.add(m.group(1));
    	}
    	e=iPattern.splitAsStream(e).reduce("",(a,b)->a+b);
    	if(e.startsWith(prefixI)){
    		code.append("echo("+codeBlock+",").append(e.substring(prefixI.length(), e.length() - postfix.length())).append(");");
    	}else{
    		code.append(e.substring(prefixE.length(), e.length() - postfix.length()).replace("echo(", "echo("+codeBlock+","));
    	}
    }


    /**
     * Compiles all java insertions on page
     */
    public void compile() {
    	StringBuilder code = new StringBuilder();
        try {
        	code.append("public void run(");
        	for(int q=0;q<types.length;q++){
        		code.append(types[q].getName()+" "+params[q]);
        		if(q<types.length-1){
        			code.append(",");
        		}
        	}
        	code.append("){");
            FileInputStream br = new FileInputStream(on);
            byte[] d=new byte[br.available()];
            br.read(d);
            br.close();
            page = new String(d);
            Matcher m = pattern.matcher(page);
            List<String> imports=new LinkedList();
            int i=0;
            while (m.find()) {
                collectEval(m.group(),code,i++,imports);
            }
            ClassBodyEvaluator evaluator=new ClassBodyEvaluator();
            evaluator.setExtendedClass(EchoClass.class);
            evaluator.setDefaultImports(imports.toArray(new String[0]));
            evaluator.cook(code.append("}").toString());
            Class cls=evaluator.getClazz();
    		obj=(EchoClass)cls.newInstance();
    		method=cls.getMethod("run", types);
    		obj.reset(i);
        } catch (Exception e) {
        	System.out.println(code.toString());
            log.error("jhtml file error",e);
        }
    }

    public PageGenProcessor(File on, Class[] types, String[] params) {
		this.types = types;
		this.params = params;
		this.on = on;
		compile();
	}
    
    public PageGenProcessor(Class[] types, String[] params) {
		this.types = types;
		this.params = params;
	}

    /**
     * @param c - array of Objects for use in page generation
     * @return Result of page generation - valid html
     */
    public String gen(Object... c) {
    	try {
			method.invoke(obj,c);
		} catch (Exception e) {
			e.printStackTrace();
		}
        Matcher m = pattern.matcher(page);
        StringBuffer sb = new StringBuffer(page.length());
        int i=0;
        while (m.find()) {
        	m.appendReplacement(sb, obj.get(i++));
        }
        m.appendTail(sb);
        return sb.toString();

    }

	public static class EchoClass{
		public void reset(int cap){
			results=new ArrayList(cap);
			for(int i=0;i<cap;i++){
				results.add(null);
			}
		}
		
		public String get(int n){
			String s=results.get(n);
			return s==null?"":s;
		}
		
    	public List<String> results;
    	
    	public void echo(int n,String s){
    		if(results.size()<=n||results.get(n)==null){
    			results.add(n, s);
    		}else{
				results.add(n, results.get(n)+s);
    		}
    	}
    	
    	public void echo(int n,boolean s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,int s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,char s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,long s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,byte s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,float s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,double s){
    		echo(n, String.valueOf(s));
    	}
    	
    	public void echo(int n,Object s){
    		echo(n, s.toString());
    	}
    }
}
