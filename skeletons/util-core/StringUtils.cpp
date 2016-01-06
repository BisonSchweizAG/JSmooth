/*
  JSmooth: a VM wrapper toolkit for Windows
  Copyright (C) 2003-2007 Rodrigo Reyes <reyes@charabia.net>

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Library General Public
  License as published by the Free Software Foundation; either
  version 2 of the License, or (at your option) any later version.
  
  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Library General Public License for more details.
  
  You should have received a copy of the GNU Library General Public
  License along with this library; if not, write to the Free
  Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
  
*/

#include "StringUtils.h"

vector<string> StringUtils::split(const string& str, const string& separators, const string& quotechars, 
				  bool handleEscape, bool returnSeparators)
{
  vector<string> result;
  string buf = "";

  for (int i=0; i<str.length(); i++)
    {
      if ( handleEscape && (str[i] == '\\')) // Found an escaped char
        {
	  i++;
	  if (i<str.length())
	    {
	      switch(str[i])
		{
		case '\'':
		case '\"':
		  buf += str[i];
		  break;
                       
		case 'n':
		  buf += '\n';
		  break;
		case 'r':
		  buf += '\r';
		  break;
		case 't':
		  buf += '\t';
		  break;
		}
	    }
        }
      else if (separators.find(str[i], 0) != separators.npos) // found a separator...
        {
	  if (buf.length() > 0)
	    {
	      result.push_back(buf);
	      buf = "";
	    }
	  
	  if (returnSeparators)
	    {
	      buf += str[i];
	      result.push_back(buf);
	      buf = "";
	    }
	  
        }
      else if (quotechars.find(str[i], 0) != quotechars.npos) // Found a quote char...
        {
	  if (buf.length() > 0)
	    {
	      result.push_back(buf);
	      buf = "";
	    }
                        
	  char qc = quotechars[ quotechars.find(str[i], 0) ];
	  i++;
	  while ( (i<str.length()) && (str[i] != qc) )
	    {
	      buf += str[i++];
	    }
        }
      else
        {
	  buf += str[i];
        }
    }
  if (buf.length() > 0)
    {
      result.push_back(buf);
    }

  return result;
}


int StringUtils::parseInt(const string& val)
{
    return atoi(val.c_str());
}

int StringUtils::parseHexa(const string& val)
{
  int result = 0;
  long multiplier = 1;

  int posmax;
  posmax = val.find_first_not_of("0123456789ABCDEFabcdef");
  if (posmax == string::npos)
    posmax=val.length();
  
  for (int i=0; i<posmax; i++, multiplier *= 16)
    {
      char c = val[posmax-i-1];
      int unit;
      if ((c>='0') && (c<='9'))
	unit = (int)(c-'0');
      else if ((c>='a') && (c<='f'))
	unit = (int)(c-'a') + 10;
      else if ((c>='A') && (c<='F'))
	unit = (int)(c-'A') + 10;

      result += (unit * multiplier);
    }
  return result;
}

string StringUtils::toString(int val)
{
    char buf[32];
    sprintf(buf, "%d", val);
    return string(buf);
}

string StringUtils::toHexString(int val)
{
    char buf[32];
    sprintf(buf, "%x", val);
    return string(buf);
}

string StringUtils::toString(const vector<string>& seq)
{
    string result = "[";
    for (vector<string>::const_iterator i=seq.begin(); i != seq.end(); i++)
    {
        result += *i;
        if ((i+1) != seq.end())
                result += ", ";
    }
    result += "]";
    return result;
}

void StringUtils::copyTo(const string& from, char* to, int length)
{
    int max = (from.length()+1>length)?length-1:from.length();
    for (int i=0; i<max; i++)
    {
        to[i] = from[i];
    }
    to[max] = 0;
}

string StringUtils::join(const vector<string>& seq, const string& separator)
{
    string result = "";
    for (vector<string>::const_iterator i=seq.begin(); i != seq.end(); i++)
    {
        result += *i;
        result += separator;
    }
    return result;
}

string StringUtils::trim(string& str)
{
    string result = "";
    int start = str.length();
    int end = 0;
    
    for (int i=0; i<str.length(); i++)
    {
        switch(str[i])
        {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                                
                   break;
                   
                default:
                   start = i;
                   i = str.length();
                   break;
        }
    }
    
    for (int i=str.length()-1; i>start; i--)
    {
        switch(str[i])
        {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                                
                   break;
                   
                default:
                   end = i;
                   i = -1;
                   break;
        }
    }   
    
    result = str.substr(start, end-start+1);    
    return result;
}

string StringUtils::replaceEnvironmentVariable(const string& str)
{
    string result = str;
    int start = 0;
    
    while ( (start=result.find('%', start)) != str.npos)
    {
        start++;
        int end = result.find('%', start);
        if (end != str.npos)
        {
                int replacelen = end - start;
                string envname = result.substr(start, replacelen);
                
                char buffer[512];
                buffer[0]=0;
                         
                GetEnvironmentVariable(envname.c_str(), buffer, 512);

                result.replace(start-1, replacelen+2, buffer);
                start+= strlen(buffer);                
        }
        else
                start = end;
    }
    
    return result;
}

string StringUtils::requote(const string& str)
{
  return StringUtils::fixQuotes( StringUtils::replace(str, "\"", "") );
}

string StringUtils::requoteForCommandLine(const string& str)
{
  std::string res = StringUtils::replace(str, "\"", "");
  if (res[res.size()-1] == '\\')
    res += "\\";
//     res = res.substr(0, res.size()-1);
  return StringUtils::fixQuotes(res);
}


string StringUtils::replace(const string& str, const string& pattern, const string& replacement)
{
    string result = str;
    int pos;
    
    while ( (pos=result.find(pattern, 0)) != result.npos )
    {
        result.replace(pos, pattern.length(), replacement);
    }
    
    return result;
}

string StringUtils::fixQuotes(const string& str)
{
  if (str.size() == 0)
    return "\"\"";
  
  if (str[0] == '"')
    return str;

  return "\"" + str + "\"";
}

std::string StringUtils::escape(const string& str)
{
  std::string result;
  for (int i=0; i<str.size(); i++)
    {
      if (str[i] == '\\')
	result += "\\\\";
      else
	result += str[i];
    }
  return result;
}

std::string StringUtils::unescape(const string& str)
{
  std::string result;
  for (int i=0; i<str.size(); i++)
    {
      if (str[i] == '\\')
	{
	  if ((i+1<str.size()) && (str[i+1]=='\\'))
	    {
	      i++;
	      result += "\\";
	    }
	  else
	    result += "\\";
	}
      else
	result += str[i];
    }
  return result;
}

string StringUtils::toLowerCase(const string& str)
{
  string result = str;
  for (int i=0; i<result.length(); i++)
    {
      if ((result[i]>='A') && (result[i]<='Z'))
	result[i] = result[i] - 'A' + 'a';
    }
  return result;
}

std::string StringUtils::fixArgumentString(const std::string& arg)
{
  string res;
  for (int i=0; i<arg.length(); i++)
    {
      char c = arg[i];
      if (c == '\\')
	{
	  if ((i + 1) < arg.length())
	    {
	      if (arg[i+1] == '"')
		{
		  res += "\\§§";
		}
	    }
	  else
	    {
	      res += '\\';
	    }
	}

      res += c;
    }
  return res;
}


std::string StringUtils::sizeToJavaString(int size)
{
  if (size > (1024*1024))
    return StringUtils::toString(size / (1024*1024)) + "m";
  else if (size > 1024)
    return StringUtils::toString(size / 1024) + "k";
  else
    return StringUtils::toString(size);
}

std::string StringUtils::sizeToJavaString(std::string size)
{
  if ( (size.find('m') != string::npos)
      || (size.find('M') != string::npos)
      || (size.find('k') != string::npos)
      || (size.find('K') != string::npos) )
    {
      return size;
    }
  else
    return sizeToJavaString(StringUtils::parseInt(size));
}
