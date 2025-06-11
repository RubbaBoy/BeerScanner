import api from './api';

// Define the ScraperSettings interface
export interface ScraperSettings {
  id?: number;
  checkInterval: number; // in minutes
  maxRetries: number;
  retryDelay: number; // in seconds
  userAgent: string;
  timeout: number; // in seconds
  enabled: boolean;
  notifyOnFailure: boolean;
  lastUpdated?: string;
}

// Get current scraper settings
export const getScraperSettings = async (): Promise<ScraperSettings> => {
  // const response = await api.get('/api/v1/admin/scraper/settings');
  // return response.data;
  return Promise.resolve({
    id: 1,
    checkInterval: 15,
    maxRetries: 3,
    retryDelay: 5,
    userAgent: 'MyScraperBot/1.0',
    timeout: 30,
    enabled: true,
    notifyOnFailure: true,
    lastUpdated: new Date().toISOString(),
  })
};

// Update scraper settings
export const updateScraperSettings = async (settings: ScraperSettings): Promise<ScraperSettings> => {
  const response = await api.put('/api/v1/admin/scraper/settings', settings);
  return response.data;
};

// Get scraper statistics
export interface ScraperStats {
  totalChecks: number;
  successfulChecks: number;
  failedChecks: number;
  totalChangesDetected: number;
  averageCheckTime: number; // in milliseconds
  lastCheckTime?: string;
}

export const getScraperStats = async (): Promise<ScraperStats> => {
  const response = await api.get('/api/v1/admin/scraper/stats');
  return response.data;
};

export const getScraperStatsForBar = async (barId: number): Promise<ScraperStats> => {
  const response = await api.get(`/api/v1/admin/scraper/stats/${barId}`);
  return response.data;
};

// Reset scraper statistics
export const resetScraperStats = async (): Promise<void> => {
  await api.post('/api/v1/admin/scraper/stats/reset');
};

// Enable scraper
export const enableScraper = async (): Promise<ScraperSettings> => {
  const response = await api.post('/api/v1/admin/scraper/enable');
  return response.data;
};

// Disable scraper
export const disableScraper = async (): Promise<ScraperSettings> => {
  const response = await api.post('/api/v1/admin/scraper/disable');
  return response.data;
};