// Type definitions for the Beer Scanner application

// Bar type
export interface Bar {
  id?: number;
  name: string;
  location: string;
  menuUrl?: string;
  menuXPath?: string;
  lastMenuHash?: string;
  lastCheckedAt?: string;
  currentBeers?: Beer[];
  pastBeers?: Beer[];
  trackedBy?: User[];
  checks?: BarCheck[];
  requestedBy?: User;
  createdAt?: string;
  updatedAt?: string;
  approved?: boolean;
}

export interface BarSimple {
  id: number
  name: string
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

export interface BeerExtended extends Beer {
  availableAt: BarSimple[];
  previouslyAvailableAt: BarSimple[];
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
export type PageBarCheck = Page<BarCheck>;