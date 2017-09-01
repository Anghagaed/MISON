#ifndef PARSE_HANG_COLIN_YUN
#define PARSE_HANG_COLIN_YUN
#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <functional>
#include "Bitmaps.h"


struct FieldInfo {
	// 0 = Not Array Field
	// 1 = Array Field : Grab Everything
	// 2 = Array Field : Grab a list defined by variable
	int type;
	vector<string> subField;
	FieldInfo(int type = 0, string field = "empty");
	FieldInfo();
};

typedef unordered_map<unsigned, vector<string>* > arraymap;
typedef unordered_map<unsigned, FieldInfo> hashmap;

using namespace std;

class parser {
private:
	hashmap queryFields;
	string line;
	bitmaps words;
	int hashCheck;
private:
	int parseWord(int level, string append, vector<int>& colonP, vector<pair<string, string> >& result);
public:
	parser(hashmap queryFields);
	int parseWord(string line, bitmaps words, vector<pair<string, string> >& result);

};
#endif