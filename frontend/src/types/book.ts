export type GoogleBook = {
  googleVolumeId: string;
  title: string;
  authors?: string;     // 👈 string
  thumbnail?: string;
  publishedDate?: string;
  description?: string;
};

export type BookSearchResponse = {
  items: GoogleBook[];
  page: number;
  size: number;
  total: number;
};

export type ReadBookResponse = {
  googleVolumeId: string;
  title: string;
  authors?: string;     // 👈 string
  thumbnail?: string;
  addedAt?: string;
};

export type ReadBookCreateRequest = {
  googleVolumeId: string;
  title: string;
  authors?: string;     // 👈 string
  thumbnail?: string;
};