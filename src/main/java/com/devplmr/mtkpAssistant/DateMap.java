package com.devplmr.mtkpAssistant;

import java.util.HashMap;
import java.util.Map;

public class DateMap
{
	private Map<String, Long> dates = new HashMap<>();

	public boolean checkValue(String key, long value)
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
