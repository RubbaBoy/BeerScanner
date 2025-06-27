// Type definitions for the Beer Scanner application

// Bar type
export interface BarAdmin extends Bar, BarWebpageSettings {
  aiInstructions?: string
  menuUrl?: string;
  menuXPath?: string;
  lastMenuHash?: string;
  approved?: boolean;
}

export interface BarWebpageSettings {
  menuComponentXPath?: string
  ageVerificationXPath?: string
  cleanupScript?: string
  processAsText?: boolean
}

export interface Bar {
  id: number
  name: string
  location: string;
  lastCheckedAt: string;
  currentBeerCount: number
}

// Beer type
export interface Beer {
  id?: number;
  name: string;
  type?: string;
  brewery?: string;
  abv?: number;
  description?: string;
  createdAt?: string;
  updatedAt?: string;

  // Available in "extended" requests
  // availableAt?: BarSimple[];
  // previouslyAvailableAt?: BarSimple[];
}

// Data sent to update a beer
export interface BeerModify {
  name: string;
  type?: string;
  brewery?: string;
  abv?: number;
  description?: string;
}

export interface BeerExtended extends Beer {
  availableAt: Bar[];
  previouslyAvailableAt: Bar[];
}

export interface BeerAlias {
  id: number;
  name: string;
  brewery: string;
  beerId: number;
  createdAt: string;
  updatedAt: string;
}

export interface BeerAliasAdd {
  name: string;
  brewery: string;
}

// Beer request type
export interface BeerRequest {
  id?: number;
  name: string;
  type?: string;
  brewery?: string;
  abv?: number;
  description?: string;
  requestedBy: User;
  requestedAt?: string;
}

// User type
export interface User {
  id?: number;
  email: string;
  name: string;
  googleId?: string;
  profilePicture?: string;
  notificationEnabled?: boolean;
  isAdmin?: boolean;
  // trackedBars?: Bar[];
  // trackedBeers?: Beer[];
  createdAt?: string;
  updatedAt?: string;
}

// Notification type
export interface Notification {
  id?: number;
  user?: User;
  bar?: Bar;
  beer?: Beer;
  title: string;
  message: string;
  type: 'BEER_AVAILABLE' | 'MENU_CHANGED' | 'SYSTEM';
  createdAt?: string;
  sentAt?: string;
  read?: boolean;
  sent?: boolean;
}

// BarCheck type
export interface BarCheck {
  id?: number;
  bar?: Bar;
  menuHash?: string;
  menuContent?: string;
  hasChanges?: boolean;
  processDuration?: number; // in milliseconds
  processingStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  errorMessage?: string;
  createdAt?: string;
}

// Pagination types
export interface Pageable {
  page: number;
  size: number;
  sort?: string[];
}

export interface PageableObject {
  offset: number;
  sort: SortObject;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  unpaged: boolean;
}

export interface SortObject {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface Page<T> {
  totalPages: number;
  totalElements: number;
  first: boolean;
  last: boolean;
  size: number;
  content: T[];
  number: number;
  numberOfElements: number;
  sort: SortObject;
  pageable: PageableObject;
  empty: boolean;
}

// Specific page types
export type PageNotification = Page<Notification>;
export type PageBeer = Page<Beer>;
export type PageBeerExtended = Page<BeerExtended>;
export type PageBeerRequest = Page<BeerRequest>;
export type PageBar = Page<Bar>;
export type PageBarAdmin = Page<BarAdmin>;
export type PageBarCheck = Page<BarCheck>;

// Converting methods (so extra data isn't sent via the API)

export const createBeerModify = (beer: Beer): BeerModify => {
  return {
    name: beer.name,
    type: beer.type,
    brewery: beer.brewery,
    abv: beer.abv,
    description: beer.description
  };
}
