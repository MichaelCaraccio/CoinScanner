package com.example.coinscanner;

import java.util.HashMap;

public class CHFStore {

	public static final Coin fiveCents = new Coin("0.05 CHF", 0.05, 8.475);
	public static final Coin tenCents = new Coin("0.10 CHF", 0.10, 9.575);
	public static final Coin twentyCents = new Coin("0.20 CHF", 0.20, 10.525);
	public static final Coin fiftyCents = new Coin("0.50 CHF", 0.50, 9.1);
	public static final Coin oneFrancs = new Coin("1 CHF", 1.0, 11.6);
	public static final Coin twoFrancs = new Coin("2 CHF", 2.0, 13.7);
	public static final Coin fiveFrancs = new Coin("5 CHF", 5.0, 15.725);

	public static HashMap<Double, Coin> getRatios(Coin selectedCoin) {
		HashMap<Double, Coin> map = new HashMap<Double, Coin>();
		Coin[] tab = getSortedCoinTab();

		for (Coin c : tab) {
			map.put(selectedCoin.getRadius() / c.getRadius(), c);
		}
		return map;
	}

	public static Coin[] getSortedCoinTab() {
		Coin[] tab = new Coin[7];
		tab[0] = fiveCents;
		tab[1] = tenCents;
		tab[2] = twentyCents;
		tab[3] = fiftyCents;
		tab[4] = oneFrancs;
		tab[5] = twoFrancs;
		tab[6] = fiveFrancs;

		return tab;
	}

}
