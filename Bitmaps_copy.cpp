#include "Bitmaps.h"

bitmaps::bitmaps() {
	
}

bitmaps::mapContainer::mapContainer(int& layers) {
	levels.reserve(layers);
	//CMlevels.reserve(layers);
	bitset<B_INT> temp;
	for (int i = 0; i < layers; ++i) {
		levels.push_back(temp);
		//CMlevels.push_back(temp);
	}
}
bitmaps::bitmaps(int& layers, vector<string>& wordSplit) {
	word = wordSplit;
	// Reserve space to avoid reallocation
	map.reserve(word.size());
	mapContainer temp(layers);
	for (int i = 0; i < word.size(); ++i) {
		map.push_back(temp);
	}

}

bool bitmaps::mirror(bitset<B_INT> set) {
	// Nothing to mirror
	if (set.count() == 0)
		return 1;
	bool temp;
	for (int i = 0; i < B_INT / 2; ++i) {
		temp = set[i];
		set[i] = set[31 - i];
		set[31 - i] = temp;
	}
	return 1;
}

bool bitmaps::mirror(int& index) {
	mirror(map[index].escapeBitset);
	mirror(map[index].quoteBitset);
	mirror(map[index].colonBitset);
	mirror(map[index].lbracketBitset);
	mirror(map[index].rbracketBitset);
	mirror(map[index].arraylbracketBitset);
	mirror(map[index].arrayrbracketBitset);
	mirror(map[index].structQBitset);
	mirror(map[index].strBitset);
	mirror(map[index].structCBitset);
	//mirror(map[index].structCMBitset);
	for (int i = 0; i < map[index].levels.size(); ++i) {
		mirror(map[index].levels[i]);
	}
	/*
	for (int i = 0; i < map[index].CMlevels.size(); ++i) {
		mirror(map[index].CMlevels[i]);
	}
*/

	return 1;
}

bool bitmaps::bitsetCreate() {

	// Phase 1 and 2
	{
		//cout << "map.size() " << map.size() << endl;
		char prev[2];
		for (int j = 0; j < map.size(); ++j) {
			for (int i = 0; i < word[j].size(); ++i) {
				char currentChar = word[j][i];
				switch (currentChar) {
				case '\\':
					map[j].escapeBitset[i] = 1;
					break;
				case '\"':
					map[j].quoteBitset[i] = 1;
					if ((prev[0] != '\\') || (prev[0] == '\\' && prev[1] == '\\')) {
						//cout << i << ": " << prev[0] << " " << prev[1] << endl;
						map[j].structQBitset[i] = 1;
					}
					break;
				case ':':
					map[j].colonBitset[i] = 1;
					break;
				case ',':
					map[j].commaBitset[i] = 1;
				case '{':
					map[j].lbracketBitset[i] = 1;
					break;
				case '}':
					map[j].rbracketBitset[i] = 1;
					break;
				case '[':
					map[j].arraylbracketBitset[i] = 1;
					break;
				case ']':
					map[j].arrayrbracketBitset[i] = 1;
				}
				prev[1] = prev[0];
				prev[0] = word[j][i];
			}
		}
	}
	// Phase 3

	{
		// Number of quotes count
		unsigned n = 0;

		// Cannot Use bitset due to bitset not supporting - 1 operation
		// bitset to_ulong convert the bits into unsigned long int unless the numbers of bits
		// overflows the function 
		for (int j = 0; j < map.size(); ++j) {
			unsigned mQuote = static_cast<unsigned> (map[j].structQBitset.to_ulong());
			unsigned mString = 0;
			// Loop until the number of quote in mQuote is 0
			for (int i = map[j].structQBitset.count(); i != 0; --i) {
				// Extract and smear the rightmost 1  m = S(mQuote) = mQuote ^ (mQuote - 1)
				unsigned m = mQuote ^ (mQuote - 1);
				// Extend mstring to the rightmost 1 mString = mString ^| m
				mString = mString ^ m;
				// remove the rightmost 1 mQuote = R(mQuote) = mQuote & (mQuote - 1)
				mQuote &= (mQuote - 1);
				++n;
			}
			// convert mString into 32-bit bitset
			bitset<B_INT> temp(mString);
			// Flip mString if necessary given n mod 2 = 1
			if (n % 2 == 1)
				temp.flip();
			map[j].strBitset = temp;
		}
	}

	for (int i = 0; i < map.size(); ++i) {
		unsigned strMask = static_cast<unsigned> (map[i].strBitset.to_ulong());
		unsigned temp;
		// Colon
		temp = static_cast<unsigned> (map[i].colonBitset.to_ulong());
		temp = temp - (temp & strMask);
		map[i].structCBitset = bitset<B_INT>(temp);
		// left Brace
		temp = static_cast<unsigned> (map[i].lbracketBitset.to_ulong());
		temp = temp - (temp & strMask);
		map[i].structLBitset = bitset<B_INT>(temp);
		// right Brace
		temp = static_cast<unsigned> (map[i].rbracketBitset.to_ulong());
		temp = temp - (temp & strMask);
		map[i].structRBitset = bitset<B_INT>(temp);



		/*
		//for support of array fields
		// comma
		temp = static_cast<unsigned> (map[i].commaBitset.to_ulong());
		temp = temp - (temp & strMask);
		map[i].structCMBitset = bitset<B_INT>(temp);
		// left bracket
		temp = static_cast<unsigned> (map[i].arraylbracketBitset.to_ulong());
		temp = temp - (temp & strMask);
		map[i].structALBBitset = bitset<B_INT>(temp);
		// right bracket
		temp = static_cast<unsigned> (map[i].arrayrbracketBitset.to_ulong());
		temp = temp - (temp & strMask);
		map[i].structARBBitset = bitset<B_INT>(temp);

		*/
	}

	//cout << "Phase 4" << endl;
	
	// Phase 4 
	{
		// copy colon bitmap to leveled colon bitmaps
		// *** NOTE: map[0].structCBitset PART MUST BE MODIFIED
		//map[0].structCBitset = bitset<B_INT>(std::string("00000100000000000000000000100000"));
		for (int i = 0; i < map.size(); ++i)
			for (int j = 0; j < map[i].levels.size(); ++j)
				map[i].levels[j] = map[i].structCBitset;
		unsigned mLeft, mRight;									// m(left), m(right)
		unsigned mLbit, mRbit;									// m(left bit), m(right bit)
		const unsigned lvls = map[0].levels.size();					// Number of nesting levels
																	//cout << "lvls: " << lvls << endl;
		stack<vector<unsigned> > S;

		for (int i = 0; i < map.size(); ++i)
		{
			mLeft = static_cast<unsigned> (map[i].lbracketBitset.to_ulong());
			mRight = static_cast<unsigned> (map[i].rbracketBitset.to_ulong());
			do 													// iterate over each right brace
			{
				// extract the rightmost 1
				mRbit = mRight & -mRight;
				mLbit = mLeft & -mLeft;

				while (mLbit != 0 && (mRbit == 0 || mLbit < mRbit))
				{
					vector<unsigned> push;						// 0 = "j", 1 = mLbit
					push.push_back(i);
					push.push_back(mLbit);
					S.push(push);								// push left bit to stack
					mLeft = mLeft & (mLeft - 1);				// remove the rightmost 1
					mLbit = mLeft & -mLeft;					// extract the rightmost 1
				}
				if (mRbit != 0)
				{

					vector<unsigned> pop = S.top();				// 0 = "j", 1 = mLbit
					mLbit = pop[1];
					S.pop();
					//cout << "m(right bit) exists i: " << i << " j: " << pop[0] << endl;
					//cout << "S.size() after pop: " << S.size() << endl;
					if (0 < S.size() && S.size() <= lvls)	// clear bits at the upper level
					{
						bitset<B_INT> flip;
						//cout << "In 0 < |S| <= l" << endl;
						if (i == pop[0])						// nested object is inside the word
						{
							//cout << "in IF" << endl;
							flip = mRbit - mLbit;
							flip.flip();
							//map[i].levels[S.size()-1] &= static_cast<unsigned> (flip.to_ulong());
							map[i].levels[S.size() - 1] &= flip;
						}
						else 									// nested object is across multiple words
						{

							map[pop[0]].levels[S.size() - 1] &= mLbit - 1;
							//cout << "map[pop[0]].levels[S.size()-1]: " << map[pop[0]].levels[S.size() - 1] << endl;
							flip = mRbit - 1;
							flip.flip();

							map[i].levels[S.size() - 1] &= flip;
							for (int k = pop[0] + 1; k < i; ++k) {
								//cout << "i " << i << " j " << pop[0] << " k " << k << endl;
								//cout << S.size() << endl;
								map[k].levels[S.size() - 1].reset();
							}
						}
					}
				}
				mRight &= mRight - 1;						// remove the rightmost 1
			} while (mRbit != 0);
		}
	}

	

	/*
	// Phase 4 
	{
		// copy colon bitmap to leveled colon bitmaps
		// *** NOTE: map[0].structCBitset PART MUST BE MODIFIED
		//map[0].structCBitset = bitset<B_INT>(std::string("00000100000000000000000000100000"));
		for (int i = 0; i < map.size(); ++i){
			for (int j = 0; j < map[i].levels.size(); ++j){
				map[i].levels[j] = map[i].structCBitset;
				//map[i].CMlevels[j] = map[i].structCMBitset;
			}
		}
		unsigned mLeft, mRight;									// m(left), m(right)
		unsigned mLbit, mRbit;									// m(left bit), m(right bit)

		//for array support
		unsigned mCMLeft, mCMRight;
		unsigned mCMLbit, mCMRbit;
		const unsigned lvls = map[0].levels.size();					// Number of nesting levels
																	//cout << "lvls: " << lvls << endl;
		stack<vector<unsigned> > S;
		stack<vector<unsigned> > SCM;

		for (int i = 0; i < map.size(); ++i)
		{
			mLeft = static_cast<unsigned> (map[i].lbracketBitset.to_ulong());
			mRight = static_cast<unsigned> (map[i].rbracketBitset.to_ulong());

			mCMLeft = static_cast<unsigned> (map[i].arraylbracketBitset.to_ulong());
			mCMRight = static_cast<unsigned> (map[i].arrayrbracketBitset.to_ulong());

			do 													// iterate over each right brace
			{
				// extract the rightmost 1
				mRbit = mRight & -mRight;
				mLbit = mLeft & -mLeft;

				while (mLbit != 0 && (mRbit == 0 || mLbit < mRbit))
				{
					vector<unsigned> push;						// 0 = "j", 1 = mLbit
					push.push_back(i);
					push.push_back(mLbit);
					S.push(push);								// push left bit to stack
					mLeft = mLeft & (mLeft - 1);				// remove the rightmost 1
					mLbit = mLeft & -mLeft;					// extract the rightmost 1
				}
				if (mRbit != 0)
				{

					vector<unsigned> pop = S.top();				// 0 = "j", 1 = mLbit
					mLbit = pop[1];
					S.pop();
					//cout << "m(right bit) exists i: " << i << " j: " << pop[0] << endl;
					//cout << "S.size() after pop: " << S.size() << endl;
					if (0 < S.size() && S.size() <= lvls)	// clear bits at the upper level
					{
						bitset<B_INT> flip;
						//cout << "In 0 < |S| <= l" << endl;
						if (i == pop[0])						// nested object is inside the word
						{
							//cout << "in IF" << endl;
							flip = mRbit - mLbit;
							flip.flip();
							//map[i].levels[S.size()-1] &= static_cast<unsigned> (flip.to_ulong());
							map[i].levels[S.size() - 1] &= flip;
						}
						else 									// nested object is across multiple words
						{

							map[pop[0]].levels[S.size() - 1] &= mLbit - 1;
							//cout << "map[pop[0]].levels[S.size()-1]: " << map[pop[0]].levels[S.size() - 1] << endl;
							flip = mRbit - 1;
							flip.flip();

							map[i].levels[S.size() - 1] &= flip;
							for (int k = pop[0] + 1; k < i; ++k) {
								//cout << "i " << i << " j " << pop[0] << " k " << k << endl;
								//cout << S.size() << endl;
								map[k].levels[S.size() - 1].reset();
							}
						}
					}
				}
				mRight &= mRight - 1;						// remove the rightmost 1
			} while (mRbit != 0);


			
			//for array support
			do 													// iterate over each right brace
			{
				// extract the rightmost 1
				mCMRbit = mCMRight & -mCMRight;
				mCMLbit = mCMLeft & -mCMLeft;

				while (mCMLbit != 0 && (mCMRbit == 0 || mCMLbit < mCMRbit))
				{
					vector<unsigned> push;						// 0 = "j", 1 = mLbit
					push.push_back(i);
					push.push_back(mCMLbit);
					SCM.push(push);								// push left bit to stack
					mCMLeft = mCMLeft & (mCMLeft - 1);				// remove the rightmost 1
					mCMLbit = mCMLeft & -mCMLeft;					// extract the rightmost 1
				}
				if (mCMRbit != 0)
				{

					vector<unsigned> pop = SCM.top();				// 0 = "j", 1 = mLbit
					mCMLbit = pop[1];
					SCM.pop();
					//cout << "m(right bit) exists i: " << i << " j: " << pop[0] << endl;
					//cout << "S.size() after pop: " << S.size() << endl;
					if (0 < SCM.size() && SCM.size() <= lvls)	// clear bits at the upper level
					{
						bitset<B_INT> flip;
						//cout << "In 0 < |S| <= l" << endl;
						if (i == pop[0])						// nested object is inside the word
						{
							//cout << "in IF" << endl;
							flip = mCMRbit - mCMLbit;
							flip.flip();
							//map[i].levels[S.size()-1] &= static_cast<unsigned> (flip.to_ulong());
							map[i].CMlevels[SCM.size() - 1] &= flip;
						}
						else 									// nested object is across multiple words
						{

							map[pop[0]].CMlevels[SCM.size() - 1] &= mCMLbit - 1;
							//cout << "map[pop[0]].levels[S.size()-1]: " << map[pop[0]].levels[S.size() - 1] << endl;
							flip = mCMRbit - 1;
							flip.flip();

							map[i].CMlevels[SCM.size() - 1] &= flip;
							for (int k = pop[0] + 1; k < i; ++k) {
								//cout << "i " << i << " j " << pop[0] << " k " << k << endl;
								//cout << S.size() << endl;
								map[k].CMlevels[SCM.size() - 1].reset();
							}
						}
					}
				}
				mCMRight &= mCMRight - 1;						// remove the rightmost 1
			} while (mCMRbit != 0);
			  
		}
	}
	*/



	
	const unsigned lvls = map[0].levels.size();
	for (int a = 0; a < map.size(); ++a) {
		for (int b = lvls - 1; b > 0; --b) {
			/*
			for (int c = 0; c < b; ++c) {
			unsigned temp1, temp2;
			temp1 = static_cast<unsigned> (map[a].levels[b].to_ulong());
			temp2 = static_cast<unsigned> (map[a].levels[c].to_ulong());
			map[a].levels[b] = temp1 - temp2;
			}
			*/
			unsigned temp1, temp2;
			temp1 = static_cast<unsigned> (map[a].levels[b].to_ulong());
			temp2 = static_cast<unsigned> (map[a].levels[b - 1].to_ulong());
			map[a].levels[b] = temp1 - (temp1 & temp2);
		
		}
	}
	/*
	const unsigned cmlvls = map[0].CMlevels.size();
	for (int a = 0; a < map.size(); ++a) {

			//for array support
			unsigned temp1, temp2;
			temp1 = static_cast<unsigned> (map[a].CMlevels[b].to_ulong());
			temp2 = static_cast<unsigned> (map[a].CMlevels[b - 1].to_ulong());
			map[a].CMlevels[b] = temp1 - (temp1 & temp2);
		
		}
	}
	*/



	return 1;
}

bool bitmaps::generateColonPositions(int start, int end, int level, vector<int>& colonPositions) {
	if (colonPositions.size())
		colonPositions.clear();
	unsigned mcolon;
	for (int i = start / B_INT; i < ceil(double(end) / B_INT); ++i) {
		mcolon = static_cast<unsigned> (map[i].levels[level].to_ulong());
		while (mcolon != 0) {
			bitset<B_INT> mbit = (mcolon & -mcolon) - 1;
			int offset = i * B_INT + mbit.count();
			if (start <= offset && offset <= end) {
				colonPositions.push_back(offset);
			}
			mcolon = mcolon & (mcolon - 1);
		}
	}
	cout << "Colon Position is: " << endl;
	for (int i = 0; i < colonPositions.size(); ++i) {
		cout << colonPositions[i] << " ";
	}
	cout << endl;
	return 1;
}



// Printing bitmaps
ostream& operator << (ostream &o, bitmaps& bm) {
	for (int i = 0; i < bm.word.size(); ++i) {
		cout << "String is: " << bm.word[i] << endl;
		cout << "Phase 1: " << endl;
		cout << "\\  bitset: " << bm.map[i].escapeBitset << endl;
		cout << "\"  bitset: " << bm.map[i].quoteBitset << endl;
		cout << ":  bitset: " << bm.map[i].colonBitset << endl;
		cout << ",  bitset: " << bm.map[i].commaBitset << endl;
		cout << "{  bitset: " << bm.map[i].lbracketBitset << endl;
		cout << "}  bitset: " << bm.map[i].rbracketBitset << endl;
		cout << "[  bitset: " << bm.map[i].arraylbracketBitset << endl;
		cout << "]  bitset: " << bm.map[i].arrayrbracketBitset << endl;
		cout << "Phase 2: " << endl;
		cout << "SQ bitset: " << bm.map[i].structQBitset << endl;
		cout << "Phase 3: " << endl;
		cout << "strbitset: " << bm.map[i].strBitset << endl;
		cout << "Phase 4: " << endl;
		cout << "SC bitset: " << bm.map[i].structCBitset << endl;
		cout << "Levels: " << bm.map[i].levels.size() << endl;
		for (int j = 0; j < bm.map[i].levels.size(); ++j) {
			cout << "L" << j << ":        ";
			cout << bm.map[i].levels[j] << endl;
		}
		cout << endl;
/*
		cout << "SCMbitset: " << bm.map[i].structCMBitset << endl;
		cout << "CMLevels: " << bm.map[i].CMlevels.size() << endl;
		for (int j = 0; j < bm.map[i].CMlevels.size(); ++j) {
			cout << "L" << j << ":        ";
			cout << bm.map[i].CMlevels[j] << endl;
		}
		cout << endl;
		*/
		//cout << "a" << endl;

	}
	//cout << "b" << endl;
}

void bitmaps::printPhase4() {
	cout << "Phase 4 Debug Prints" << endl;
	for (int i = 0; i < word.size(); ++i) {
		string temp = word[i];
		cout << "Iteration " << i << endl;
		cout << "String is: " << temp << endl;
		reverse(temp.begin(), temp.end());
		cout << "Mirror is: ";
		//<< temp << endl;
		if (temp.size() != 32) {
			for (int i = 0; i < 32 - temp.size(); ++i) {
				cout << " ";
			}
		}
		cout << temp << endl;

		cout << "SC bitset: " << map[i].structCBitset << endl;
		cout << "{  bitset: " << map[i].lbracketBitset << endl;
		for (int j = 0; j < map[i].levels.size(); ++j) {
			cout << "L" << j + 1 << ":        ";
			cout << map[i].levels[j] << endl;;
		}
		cout << "}  bitset: " << map[i].rbracketBitset << endl;

		cout << endl;
	}
}