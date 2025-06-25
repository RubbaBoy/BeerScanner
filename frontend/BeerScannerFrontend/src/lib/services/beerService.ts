import api from './api';
import { trackUserResource, untrackUserResource } from './serviceHelpers';
import type {
  Beer,
  PageBeer,
  Bar,
  Pageable,
  User,
  BeerRequest,
  PageBeerRequest,
  PageBeerExtended,
  BeerExtended,
  BeerAlias
} from '$lib/types';

// Get all beers with pagination
// Returns extended Beer with availableAt and previouslyAvailableAt
export const getAllBeers = async (pageable: Pageable): Promise<PageBeerExtended> => {
  const response = await api.get('/api/v1/beers/public', { params: pageable });
  return response.data;
};

// Get a beer by ID
// Returns extended Beer with availableAt and previouslyAvailableAt
export const getBeerById = async (id: number): Promise<BeerExtended> => {
  const response = await api.get(`/api/v1/beers/public/${id}`);
  return response.data;
};

// Get bars where a beer is available
export const getBarsWithBeer = async (beerId: number): Promise<Bar[]> => {
  const response = await api.get(`/api/v1/beers/public/${beerId}/available-at`);
  return response.data;
};

// Search beers
// Returns extended Beer with availableAt and previouslyAvailableAt
export const searchBeers = async (searchTerm: string, pageable: Pageable): Promise<PageBeerExtended> => {
  const response = await api.get('/api/v1/beers/public/search', { 
    params: { 
      searchTerm,
      ...pageable
    } 
  });
  return response.data;
};

// Get beers by type
// Returns extended Beer with availableAt and previouslyAvailableAt
export const getBeersByType = async (type: string, pageable: Pageable): Promise<PageBeerExtended> => {
  const response = await api.get('/api/v1/beers/public/by-type', { 
    params: { 
      type,
      ...pageable
    } 
  });
  return response.data;
};

// Get beers by brewery
// Returns extended Beer with availableAt and previouslyAvailableAt
export const getBeersByBrewery = async (brewery: string, pageable: Pageable): Promise<PageBeerExtended> => {
  const response = await api.get('/api/v1/beers/public/by-brewery', { 
    params: { 
      brewery,
      ...pageable
    } 
  });
  return response.data;
};

// Track a beer (for authenticated users)
export const trackBeer = async (beerId: number, barId?: number): Promise<User> => {
  const params = barId ? { barId } : {};
  return trackUserResource('beers', beerId, params);
};

// Request and track a new beer
export const requestBeer = async (beer: BeerRequest): Promise<void> => {
  await api.post('/api/v1/beers/request', beer);
};


// Get user's requested beers
export const getMyBeerRequests = async (pageable: Pageable): Promise<PageBeerRequest> => {
  const response = await api.get('/api/v1/beers/my-requests', {
    params: pageable
  });
  return response.data;
};

// Untrack a beer (for authenticated users)
export const untrackBeer = async (beerId: number): Promise<User> => {
  return untrackUserResource('beers', beerId);
};

// Admin functions
// Create a beer (admin only)
export const createBeer = async (beer: Beer): Promise<Beer> => {
  const response = await api.post('/api/v1/admin/beers', beer);
  return response.data;
};

// Create a beer (admin only)
export const getRequestedBeers = async (pageable: Pageable): Promise<PageBeerRequest> => {
  const response = await api.get('/api/v1/admin/beers/unapproved', {
    params: pageable
  });
  return response.data;
};

// Approve a requested beer (admin only)
export const approveBeerRequest = async (beerRequestId: number): Promise<Beer> => {
  const response = await api.put(`/api/v1/admin/beers/${beerRequestId}/approve`);
  return response.data;
};

// Unapprove a requested beer (admin only)
export const unapproveBeerRequest = async (beerRequestId: number): Promise<Beer> => {
  const response = await api.put(`/api/v1/admin/beers/${beerRequestId}/unapprove`);
  return response.data;
};

// Find or create a beer (admin only)
export const findOrCreateBeer = async (
  name: string, 
  brewery?: string, 
  type?: string, 
  abv?: number,
  description?: string
): Promise<Beer> => {
  const response = await api.post('/api/v1/admin/beers/find-or-create', null, { 
    params: { 
      name,
      brewery,
      type,
      abv,
      description
    } 
  });
  return response.data;
};

// Update a beer (admin only)
export const updateBeer = async (id: number, beer: Beer): Promise<Beer> => {
  const response = await api.put(`/api/v1/admin/beers/${id}`, beer);
  return response.data;
};

// Delete a beer (admin only)
export const deleteBeer = async (id: number): Promise<void> => {
  await api.delete(`/api/v1/admin/beers/${id}`);
};

const beerTypeAbbreviations = [
  [
    "APA",
    "American Pale Ale"
  ],
  [
    "ESB",
    "Extra Special Bitter"
  ],
  [
    "IPA",
    "India Pale Ale"
  ],
  [
    "DIPA",
    "Double IPA"
  ],
  [
    "NEIPA",
    "New England IPA"
  ],
  [
    "RIS",
    "Russian Imperial Stout"
  ],
  [
    "FES",
    "Foreign Extra Stout"
  ],
  [
    "BDSA",
    "Belgian Dark Strong Ale"
  ],
  [
    "NA",
    "Non-Alcoholic Beer",
    "Low-Alcohol"
  ]
]

const beerTypesUnflat = [
  "Lager",
  "Pilsner",
  "German Pilsner",
  "Bohemian Pilsner",
  "American Lager",
  "Light Lager",
  "Dark Lager",
  "Bock",
  "Doppelbock",
  "Eisbock",
  [
    "Maibock",
    "Helles Bock"
  ],
  "Traditional Bock",
  "Dunkel",
  "Schwarzbier",
  "Marzen",
  "Oktoberfest",
  "Rauchbier",
  "Vienna Lager",
  "Kellerbier",
  "Zwickelbier",
  "Ale",
  "Pale Ale",
  [
    "APA",
    "American Pale Ale"
  ],
  "English Bitter",
  [
    "ESB",
    "Extra Special Bitter"
  ],
  [
    "IPA",
    "India Pale Ale"
  ],
  "American IPA",
  "English IPA",
  [
    "DIPA",
    "Double IPA"
  ],
  "Imperial IPA",
  "Triple IPA",
  [
    "NEIPA",
    "New England IPA"
  ],
  "Hazy IPA",
  "West Coast IPA",
  "Session IPA",
  "Black IPA",
  "Belgian IPA",
  "Brut IPA",
  "Milkshake IPA",
  "Brown Ale",
  "American Brown Ale",
  "English Brown Ale",
  "Mild Ale",
  "Porter",
  "American Porter",
  "English Porter",
  "Baltic Porter",
  "Robust Porter",
  "Stout",
  "American Stout",
  [
    "Irish Stout",
    "Dry Stout"
  ],
  [
    "Milk Stout",
    "Sweet Stout"
  ],
  "Oatmeal Stout",
  [
    "RIS",
    "Russian Imperial Stout"
  ],
  "Imperial Stout",
  "Pastry Stout",
  "Oyster Stout",
  [
    "FES",
    "Foreign Extra Stout"
  ],
  "Belgian Ale",
  "Belgian Blond Ale",
  "Belgian Dubbel",
  "Belgian Tripel",
  "Belgian Quadrupel",
  [
    "BDSA",
    "Belgian Dark Strong Ale"
  ],
  "Saison",
  "Farmhouse Ale",
  "Bière de Garde",
  "Witbier",
  "Belgian White",
  "Flanders Red Ale",
  "Oud Bruin",
  "Lambic",
  "Gueuze",
  "Fruit Lambic",
  "Faro",
  [
    "Cherry Lambic",
    "Kriek"
  ],
  [
    "Raspberry Lambic",
    "Framboise"
  ],
  "German Ale",
  "Kölsch",
  "Altbier",
  "Hefeweizen",
  "Dunkelweizen",
  "Weizenbock",
  "Kristallweizen",
  "Berliner Weisse",
  "Gose",
  "Scottish Ale",
  [
    "Scotch Ale",
    "Wee Heavy"
  ],
  "Irish Red Ale",
  "Barleywine",
  "American Barleywine",
  "English Barleywine",
  "Wheat Ale",
  "American Wheat Ale",
  "Cream Ale",
  "Blonde Ale",
  [
    "California Common",
    "Steam Beer"
  ],
  "Rye Beer",
  "Smoked Beer",
  "Fruit Beer",
  "Herb and Spice Beer",
  "Pumpkin Ale",
  "Chile Beer",
  "Christmas/Winter Specialty Spiced Beer",
  "Honey Beer",
  "Coffee Beer",
  "Chocolate Beer",
  "Sour Ale (General)",
  "Wild Ale (General)",
  "Brett Beer",
  [
    "NA",
    "Non-Alcoholic Beer",
    "Low-Alcohol"
  ]
]

/**
 * If the beer has a type that has an abbreviation, return the abbreviation. If not, return the type as is.
 *
 * @param type The beer type to get the abbreviation for
 */
// export const getAbbreviationForBeerType = (type: string): string => {
//   for (let names of beerTypeAbbreviations) {
//     if (names.includes(type)) {
//       return names[0]
//     }
//   }
//
//   return type
// }

export const getBeerTypes = (): string[] => {
  // Flatten the nested arrays and remove duplicates
  const flattenedTypes = beerTypesUnflat.flat();

  // Remove duplicates while preserving order
  return Array.from(new Set(flattenedTypes));
}

// Beer Aliases functions

// Get all aliases for a beer
export const getBeerAliases = async (beerId: number): Promise<BeerAlias[]> => {
  const response = await api.get(`/api/v1/admin/beers/${beerId}/aliases`);
  return response.data;
};

// Add an alias to a beer
export const addBeerAlias = async (beerId: number, name: string, brewery: string): Promise<BeerAlias> => {
  const response = await api.post(`/api/v1/admin/beers/${beerId}/aliases`, {
    name,
    brewery
  });
  return response.data;
};

// Delete an alias from a beer
export const deleteBeerAlias = async (aliasId: number): Promise<void> => {
  await api.delete(`/api/v1/admin/beers/aliases/${aliasId}`);
};
