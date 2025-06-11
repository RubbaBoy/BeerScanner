import api from './api';
import type { Pageable } from '$lib/types';

// Define the Log interface
export interface Log {
  id?: number;
  level: 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL';
  message: string;
  source: string;
  stackTrace?: string;
  timestamp: string;
}

// Define the PageLog interface
export interface PageLog {
  content: Log[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// Get logs with pagination
export const getLogs = async (pageable: Pageable): Promise<PageLog> => {
  const response = await api.get('/api/v1/admin/logs', { params: pageable });
  return response.data;
};

// Get logs by level
export const getLogsByLevel = async (level: string, pageable: Pageable): Promise<PageLog> => {
  const response = await api.get('/api/v1/admin/logs/by-level', { 
    params: { 
      level,
      ...pageable
    } 
  });
  return response.data;
};

// Get logs by date range
export const getLogsByDateRange = async (
  startDate: string, 
  endDate: string, 
  pageable: Pageable
): Promise<PageLog> => {
  const response = await api.get('/api/v1/admin/logs/by-date-range', { 
    params: { 
      startDate,
      endDate,
      ...pageable
    } 
  });
  return response.data;
};

// Clear logs
export const clearLogs = async (): Promise<void> => {
  await api.delete('/api/v1/admin/logs');
};