import {formatDistanceToNow} from "date-fns";

export const formatFromNowDate = (dateString: string | undefined, nullDateString = 'Never') => {
  return dateString ? formatDistanceToNow(new Date(dateString), { addSuffix: true }) : 'Never'
};
