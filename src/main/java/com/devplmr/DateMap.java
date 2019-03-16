package com.devplmr;

import java.util.HashMap;
import java.util.Map;

public class DateMap
{
	public static Map<String, Long> dates = new HashMap<String, Long>();

	public static boolean checkChangeMap(String key, long value)
	{
		long _value;

		if (dates.get(key) != null)
		{
			_value = dates.get(key);

			if (_value == value)
			{
				return false;
			}
			else
			{
				if (value > _value)
				{
					dates.replace(key, _value, value);
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			dates.put(key, value);
			return true;
		}
	}
}
