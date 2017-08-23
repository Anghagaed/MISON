#ifndef BITMAPS_HANG_COLIN_YUN
#define BITMAPS_HANG_COLIN_YUN
#include <string>
#include <iostream>
#include <vector>
#include <bitset>
#include <cassert>
#include <stack>
#include <algorithm>
#include <unordered_map>
#include <functional>
#include <math.h>

#define B_INT 32

using namespace std;

const bitset<B_INT> all1(0xFFFFFFFF);
const bitset<B_INT> all0;

/*
* Step 1 : Implements bitmap for the following operators:
* \, ", :, {, }.
*/
class bitmaps {
private:
	struct mapContainer {
		bitset<B_INT> escapeBitset, quoteBitset, colonBitset, commaBitset,
			lbracketBitset, rbracketBitset, arraylbracketBitset, arrayrbracketBitset, structQBitset,
			strBitset, structCBitset, structLBitset, structRBitset, structCMBitset, structALBBitset, structARBBitset;
		vector<bitset<B_INT> > levels;
		vector<bitset<B_INT> > CMlevels;
		mapContainer(int& layers, int& arraylayers);
	};
	// Size of map and word will always be the same. Each map is the bitmap associated with each word
	vector<mapContainer> map;
	vector<string> word;
	bool mirror(bitset<B_INT>);

public:
	bitmaps();
	bitmaps(int&, int&, vector<string>&);

	/* Return 1 for success, 0 for failure
	* Create bitmap for MISON structural indexes according
	* to Section 4.2
	*/
	bool bitsetCreate();

	/* Mirror all the bitsets given the index of the map
	* Use for sanity testing sake
	* No abuse, takes forever
	* Returns 1 for success, 0 for failure
	*/
	bool mirror(int& index);

	// Debug Prints Phase 4
	void printPhase4();

	/* Generate a list of colon positions at a given level
	*
	*/
	bool generateColonPositions(int start, int end, int level, vector<int>& colonPositions);

	// Printing bitmaps
	friend ostream& operator << (ostream&, bitmaps&);
};
#endif