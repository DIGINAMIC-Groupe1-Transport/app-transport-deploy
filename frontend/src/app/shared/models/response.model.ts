export interface ApiResponse<T> {
  status: string;
  data: T;
  message: string;
  errors: any;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  last: boolean;
}