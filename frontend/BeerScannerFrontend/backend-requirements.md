# Backend Requirements for BeerScanner Frontend

This document outlines the backend requirements needed to support the BeerScanner frontend implementation. It includes API endpoints, request/response formats, and any other backend-specific considerations.

## API Endpoints

The frontend expects the following API endpoints to be available:

### Beer Management

#### Get All Beers
- **Endpoint**: `/api/v1/beers/public`
- **Method**: GET
- **Parameters**: 
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria (e.g., `["name,asc"]`)
- **Response**: PageBeer object containing a list of beers with pagination information

#### Get Beer by ID
- **Endpoint**: `/api/v1/beers/public/{id}`
- **Method**: GET
- **Parameters**: 
  - `id` (integer): Beer ID
- **Response**: Beer object

#### Search Beers
- **Endpoint**: `/api/v1/beers/public/search`
- **Method**: GET
- **Parameters**: 
  - `searchTerm` (string): Search term
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageBeer object containing a list of beers with pagination information

#### Get Beers by Type
- **Endpoint**: `/api/v1/beers/public/by-type`
- **Method**: GET
- **Parameters**: 
  - `type` (string): Beer type
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageBeer object containing a list of beers with pagination information

#### Get Beers by Brewery
- **Endpoint**: `/api/v1/beers/public/by-brewery`
- **Method**: GET
- **Parameters**: 
  - `brewery` (string): Brewery name
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageBeer object containing a list of beers with pagination information

#### Get Bars with Beer
- **Endpoint**: `/api/v1/beers/public/{beerId}/available-at`
- **Method**: GET
- **Parameters**: 
  - `beerId` (integer): Beer ID
- **Response**: Array of Bar objects

#### Request a New Beer
- **Endpoint**: `/api/v1/beers/request`
- **Method**: POST
- **Body**: Beer object (with at least name field)
- **Parameters**: 
  - `barId` (integer, optional): Bar ID (if requesting at a specific bar)
- **Response**: Created Beer object

#### Create Beer (Admin)
- **Endpoint**: `/api/v1/admin/beers`
- **Method**: POST
- **Body**: Beer object
- **Response**: Created Beer object

#### Update Beer (Admin)
- **Endpoint**: `/api/v1/admin/beers/{id}`
- **Method**: PUT
- **Parameters**: 
  - `id` (integer): Beer ID
- **Body**: Beer object
- **Response**: Updated Beer object

#### Delete Beer (Admin)
- **Endpoint**: `/api/v1/admin/beers/{id}`
- **Method**: DELETE
- **Parameters**: 
  - `id` (integer): Beer ID
- **Response**: 200 OK

#### Find or Create Beer (Admin)
- **Endpoint**: `/api/v1/admin/beers/find-or-create`
- **Method**: POST
- **Parameters**: 
  - `name` (string): Beer name
  - `brewery` (string, optional): Brewery name
  - `type` (string, optional): Beer type
  - `abv` (number, optional): Alcohol by volume
- **Response**: Beer object

### Bar Management

#### Get All Bars
- **Endpoint**: `/api/v1/bars/public`
- **Method**: GET
- **Parameters**: 
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageBar object containing a list of bars with pagination information

#### Get Bar by ID
- **Endpoint**: `/api/v1/bars/public/{id}`
- **Method**: GET
- **Parameters**: 
  - `id` (integer): Bar ID
- **Response**: Bar object

#### Get Current Beers for Bar
- **Endpoint**: `/api/v1/bars/public/{barId}/current-beers`
- **Method**: GET
- **Parameters**: 
  - `barId` (integer): Bar ID
- **Response**: Array of Beer objects

#### Get Past Beers for Bar
- **Endpoint**: `/api/v1/bars/public/{barId}/past-beers`
- **Method**: GET
- **Parameters**: 
  - `barId` (integer): Bar ID
- **Response**: Array of Beer objects

#### Search Bars
- **Endpoint**: `/api/v1/bars/public/search`
- **Method**: GET
- **Parameters**: 
  - `searchTerm` (string): Search term
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageBar object containing a list of bars with pagination information

#### Get User's Requested Bars
- **Endpoint**: `/api/v1/bars/my-requests`
- **Method**: GET
- **Response**: Array of Bar objects

#### Request a New Bar
- **Endpoint**: `/api/v1/bars`
- **Method**: POST
- **Body**: Bar object
- **Response**: Created Bar object

#### Update Bar (Admin)
- **Endpoint**: `/api/v1/admin/bars/{id}`
- **Method**: PUT
- **Parameters**: 
  - `id` (integer): Bar ID
- **Body**: Bar object
- **Response**: Updated Bar object

#### Delete Bar (Admin)
- **Endpoint**: `/api/v1/admin/bars/{id}`
- **Method**: DELETE
- **Parameters**: 
  - `id` (integer): Bar ID
- **Response**: 200 OK

#### Approve Bar (Admin)
- **Endpoint**: `/api/v1/admin/bars/{id}/approve`
- **Method**: POST
- **Parameters**: 
  - `id` (integer): Bar ID
- **Response**: Updated Bar object

#### Get Unapproved Bars (Admin)
- **Endpoint**: `/api/v1/admin/bars/unapproved`
- **Method**: GET
- **Parameters**: 
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageBar object containing a list of unapproved bars with pagination information

### User Management

#### Get Current User
- **Endpoint**: `/api/v1/users/me`
- **Method**: GET
- **Response**: User object

#### Get Tracked Bars
- **Endpoint**: `/api/v1/users/me/tracked-bars`
- **Method**: GET
- **Response**: Array of Bar objects

#### Get Tracked Beers
- **Endpoint**: `/api/v1/users/me/tracked-beers`
- **Method**: GET
- **Response**: Array of Beer objects

#### Track Bar
- **Endpoint**: `/api/v1/users/me/tracked-bars/{barId}`
- **Method**: POST
- **Parameters**: 
  - `barId` (integer): Bar ID
- **Response**: Updated User object

#### Untrack Bar
- **Endpoint**: `/api/v1/users/me/tracked-bars/{barId}`
- **Method**: DELETE
- **Parameters**: 
  - `barId` (integer): Bar ID
- **Response**: Updated User object

#### Track Beer
- **Endpoint**: `/api/v1/users/me/tracked-beers/{beerId}`
- **Method**: POST
- **Parameters**: 
  - `beerId` (integer): Beer ID
  - `barId` (integer, optional): Bar ID (if tracking at a specific bar)
- **Response**: Updated User object

#### Untrack Beer
- **Endpoint**: `/api/v1/users/me/tracked-beers/{beerId}`
- **Method**: DELETE
- **Parameters**: 
  - `beerId` (integer): Beer ID
  - `barId` (integer, optional): Bar ID (if untracking at a specific bar)
- **Response**: Updated User object

#### Update Notification Settings
- **Endpoint**: `/api/v1/users/me/notification-settings`
- **Method**: PUT
- **Body**: Object with notification settings (key-value pairs of setting name and boolean value)
- **Response**: Updated User object

### Notification Management

#### Get Notifications
- **Endpoint**: `/api/v1/notifications`
- **Method**: GET
- **Parameters**: 
  - `page` (integer): Page number for pagination
  - `size` (integer): Number of items per page
  - `sort` (array): Sorting criteria
- **Response**: PageNotification object containing a list of notifications with pagination information

#### Get Unread Notifications
- **Endpoint**: `/api/v1/notifications/unread`
- **Method**: GET
- **Response**: Array of Notification objects

#### Count Unread Notifications
- **Endpoint**: `/api/v1/notifications/unread/count`
- **Method**: GET
- **Response**: Object with count information

#### Mark Notification as Read
- **Endpoint**: `/api/v1/notifications/{id}/read`
- **Method**: PUT
- **Parameters**: 
  - `id` (integer): Notification ID
- **Response**: Updated Notification object

#### Send System Notification (Admin)
- **Endpoint**: `/api/v1/notifications/admin/system`
- **Method**: POST
- **Body**: Object with notification data
- **Response**: Object with result information

#### Process Unsent Notifications (Admin)
- **Endpoint**: `/api/v1/notifications/admin/process-unsent`
- **Method**: POST
- **Response**: Object with result information

## Data Models

### Beer
```typescript
interface Beer {
  id?: number;
  name: string;
  type?: string;
  brewery?: string;
  abv?: number;
  description?: string;
}
```

### Bar
```typescript
interface Bar {
  id?: number;
  name: string;
  location: string;
  menuUrl?: string;
  menuXPath?: string;
  approved?: boolean;
}
```

### User
```typescript
interface User {
  id?: number;
  email: string;
  name: string;
  profilePicture?: string;
  notificationEnabled?: boolean;
  trackedBars?: Bar[];
  trackedBeers?: Beer[];
}
```

### Notification
```typescript
interface Notification {
  id?: number;
  title: string;
  message: string;
  type: 'BEER_AVAILABLE' | 'MENU_CHANGED' | 'SYSTEM';
  createdAt?: string;
  sentAt?: string;
  read?: boolean;
  sent?: boolean;
}
```

### Pagination
```typescript
interface Pageable {
  page: number;
  size: number;
  sort?: string[];
}

interface Page<T> {
  totalPages: number;
  totalElements: number;
  content: T[];
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

type PageBeer = Page<Beer>;
type PageBar = Page<Bar>;
type PageNotification = Page<Notification>;
```

## Authentication

The frontend expects JWT-based authentication with the following:

- Token should be stored in localStorage as 'token'
- All authenticated requests should include the token in the Authorization header: `Authorization: Bearer <token>`
- If a token is invalid or expired, the backend should return a 401 Unauthorized response

## Error Handling

The backend should return appropriate HTTP status codes and error messages:

- 400 Bad Request: For invalid input
- 401 Unauthorized: For authentication errors
- 403 Forbidden: For permission errors
- 404 Not Found: For resources that don't exist
- 500 Internal Server Error: For server errors

Error responses should include a message field explaining the error.

## Notes for Implementation

1. The frontend assumes that the backend will handle proper validation of all inputs.
2. The backend should implement proper authorization checks to ensure users can only access their own data and admin endpoints are restricted to admin users.
3. For beer tracking, the backend should support tracking a beer at any bar (general tracking) or at a specific bar.
4. The notification system should send notifications to users when tracked beers become available or when bar menus change.