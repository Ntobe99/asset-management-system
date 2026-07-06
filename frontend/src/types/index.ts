export type AssetStatus = 'AVAILABLE' | 'ASSIGNED' | 'IN_MAINTENANCE' | 'RETIRED' | 'DISPOSED'

export type MaintenanceType = 'PREVENTIVE' | 'CORRECTIVE' | 'INSPECTION' | 'CALIBRATION' | 'OTHER'

export type MaintenanceStatus = 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'

export interface Category {
  id: number
  name: string
  description?: string | null
  assetCount?: number
}

export interface Employee {
  id: number
  firstName: string
  lastName: string
  email?: string | null
  department?: string | null
  position?: string | null
  active: boolean
  assignedAssetCount?: number
}

export interface Asset {
  id: number
  assetTag: string
  name: string
  description?: string | null
  categoryId: number | null
  categoryName?: string | null
  status: AssetStatus
  location?: string | null
  manufacturer?: string | null
  modelNumber?: string | null
  serialNumber?: string | null
  purchaseDate?: string | null
  purchaseCost?: number | null
  currentValue?: number | null
  warrantyExpiry?: string | null
  assignedEmployeeId?: number | null
  assignedEmployeeName?: string | null
}

export interface MaintenanceRecord {
  id: number
  assetId: number
  assetName?: string
  assetTag?: string
  type: MaintenanceType
  status: MaintenanceStatus
  description?: string | null
  scheduledDate?: string | null
  completedDate?: string | null
  cost?: number | null
  performedBy?: string | null
  vendor?: string | null
  notes?: string | null
}

export interface DashboardStats {
  totalAssets: number
  availableAssets: number
  assignedAssets: number
  inMaintenanceAssets: number
  retiredAssets: number
  totalAssetValue: number
  upcomingMaintenanceCount: number
  assetsByCategory: Record<string, number>
  upcomingMaintenance: MaintenanceRecord[]
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface AuthUser {
  token: string
  username: string
  email: string
  fullName?: string
  role: 'ADMIN' | 'MANAGER' | 'USER'
}
