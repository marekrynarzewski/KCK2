package inny;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Eval
{
	public static Object evaluate(String toEvaluate) throws ScriptException
	{
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine engine = manager.getEngineByName("js");        
		Object result = engine.eval(toEvaluate);
		return result;
	}
}
