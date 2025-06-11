import { writable } from 'svelte/store';
import type { Bar, Beer } from '$lib/types';
import {getAllBars, getTrackedBars, getTrackedBeers} from "$lib/services";

export const trackedBars = writable<Bar[]>([]);
export const trackedBeers = writable<Beer[]>([]);

export const allBars = writable<Bar[]>([]);

export const refreshTrackedStore = async () => {
  const [bars, beers] = await Promise.all([
    getTrackedBars(),
    getTrackedBeers()
  ]);

  trackedBars.set(bars);
  trackedBeers.set(beers);
};

export const refreshTrackedBeers = async () => {
  const beers = await getTrackedBeers();
  trackedBeers.set(beers);
}

export const refreshTrackedBars = async () => {
  const bars = await getTrackedBars();
  trackedBars.set(bars);
};
